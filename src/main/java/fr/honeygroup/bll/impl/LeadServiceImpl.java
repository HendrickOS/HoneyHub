package fr.honeygroup.bll.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.honeygroup.bll.LeadService;
import fr.honeygroup.bo.DemandeLead;
import fr.honeygroup.bo.DetailsSpecifiques;
import fr.honeygroup.bo.Prestation;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.request.LeadRequest;
import fr.honeygroup.bo.response.LeadResponse;
import fr.honeygroup.mapper.LeadMapper;
import fr.honeygroup.repository.DemandeLeadRepository;
import fr.honeygroup.repository.PrestationRepository;
import fr.honeygroup.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * Implémentation du service métier gérant le flux d'acquisition des opportunités d'affaires (Lead).
 * <p>
 * Cette classe orchestre la capture des demandes d'informations ou expressions de besoins 
 * formulées par les prospects, applique une validation sur les critères requis, et construit 
 * l'arborescence relationnelle dynamique pour les détails sur-mesure (approche NoSQL/EAV).
 * </p>
 */
@Service
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService {

    private final DemandeLeadRepository demandeLeadRepository;
    private final UserRepository userRepository;
    private final PrestationRepository prestationRepository;
    private final LeadMapper leadMapper;

    /**
     * Crée et enregistre un nouveau dossier de prospection (Lead) avec ses spécifications personnalisées.
     * <p>
     * La méthode valide la présence de critères optionnels, associe l'utilisateur demandeur ainsi 
     * que la prestation catalogue ciblée, puis convertit la structure clé/valeur reçue en entités de 
     * détails persistables rattachées bidirectionnellement au lead parent.
     * </p>
     * * @param request Objet DTO contenant l'identité du prospect, la prestation et la cartographie des besoins spécifiques.
     * @return Le {@link LeadResponse} enrichi et converti après insertion réussie en base de données.
     * @throws RuntimeException Si la structure des détails est vide ou absente, ou si l'utilisateur ou la prestation 
     * cibles s'avèrent introuvables.
     */
    @Override
    @Transactional
    public LeadResponse createLead(LeadRequest request) {

        // Validation défensive des données complémentaires soumises par le formulaire
        validateDetails(request.getSpecificDetails());

        // Chargement du compte client associé à l'opportunité commerciale
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User introuvable"));

        // Récupération de la prestation cible (permet également d'en déduire indirectement le pôle d'activité parent)
        Prestation prestation = prestationRepository.findById(request.getPrestationId())
                .orElseThrow(() -> new RuntimeException("Prestation introuvable"));

        // Construction du dossier de lead initial via le Builder pattern
        DemandeLead lead = DemandeLead.builder()
                .user(user)
                .prestation(prestation)
                .source(request.getSource())
                .build();

        // Mapping et pivotement fonctionnel du dictionnaire de clés/valeurs vers la table des spécifications relationnelles
        List<DetailsSpecifiques> detailsList = request.getSpecificDetails()
                .entrySet()
                .stream()
                .map(entry -> DetailsSpecifiques.builder()
                        .champCle(entry.getKey())
                        .valeur(entry.getValue())
                        .demandeLead(lead) // Association de la clé étrangère bidirectionnelle
                        .build()
                )
                .toList();

        // Injection de la collection de spécifications qualifiées au sein de l'agrégat parent
        lead.setSpecificDetails(detailsList);

        // Persistance de l'arbre d'entités complet (la cascade propage l'enregistrement des lignes détails rattachées)
        DemandeLead saveDemandeLead = demandeLeadRepository.save(lead);

        // Transformation et extraction sécurisée vers le format de réponse de l'API
        return leadMapper.toResponse(saveDemandeLead);
    }

    /**
     * Règle de validation de cohérence interne.
     * <p>
     * Bloque le traitement en amont si aucune caractéristique ou note spécifique n'accompagne 
     * l'expression de besoin sur-mesure du formulaire de contact.
     * </p>
     * * @param details La cartographie dictionnaire des critères techniques ou organisationnels fournis.
     * @throws RuntimeException Si le dictionnaire s'avère nul ou ne contient aucun élément.
     */
    private void validateDetails(Map<String, String> details) {
        if (details == null || details.isEmpty()) {
            throw new RuntimeException("Details obligatoires");
        }
    }
}