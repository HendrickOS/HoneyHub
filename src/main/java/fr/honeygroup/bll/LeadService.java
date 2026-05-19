package fr.honeygroup.bll;

import fr.honeygroup.bo.request.LeadRequest;
import fr.honeygroup.bo.response.LeadResponse;

import java.util.List;

/**
 * Contrat d'interface definissant la logique metier associee aux opportunites commerciales (Leads).
 * <p>
 * Ce service centralise le traitement transactionnel des expressions de besoins des prospects.
 * Il assure l'unification des flux de prospection et la manipulation des donnees dynamiques 
 * liees aux differents formulaires metiers de Honey Group.
 * </p>
 */
public interface LeadService {

    /**
     * Traite et persiste une nouvelle opportunite commerciale (Lead) dans le systeme.
     * <p>
     * Cette methode extrait les metadonnees du prospect et parse les attributs variables 
     * du formulaire de contact (approche EAV) pour generer un historique commercial exploitable.
     * </p>
     * * @param request Le DTO LeadRequest contenant l'identite du prospect et le bloc de details specifiques.
     * @return Le DTO LeadResponse formalisant l'enregistrement et la structure complete du lead cree.
     */
    LeadResponse createLead(LeadRequest request);

    /**
     * Recupere la liste exhaustive de l'ensemble des leads references pour le suivi commercial.
     * <p>
     * Fournit une vue globale consolidee destinee exclusivements aux tableaux de bord du Staff.
     * </p>
     * * @return Une liste de DTOs LeadResponse representant la totalite du tunnel de prospection.
     */
    List<LeadResponse> getAllLeads();

    /**
     * Recherche et extrait le dossier complet d'un lead specifique via son identifiant unique.
     * <p>
     * Permet d'isoler une fiche de prospection pour en analyser les besoins techniques ou ecoutouristiques.
     * </p>
     * * @param id L'identifiant unique du lead recherche en base de donnees.
     * @return Le DTO LeadResponse correspondant a la ressource trouvee.
     */
    LeadResponse getLeadById(Long id);

    /**
     * Modifie l'etat d'avancement d'un lead au sein du cycle de conversion commerciale.
     * <p>
     * Assure le controle de la transition vers le nouveau statut (ex: en cours de traitement, 
     * transforme en client, archive) pour le suivi de la performance de l'equipe.
     * </p>
     * * @param id L'identifiant unique du lead a mettre a jour.
     * @param statut La nouvelle valeur de l'enumeration StatutLead a appliquer.
     * @return Le DTO LeadResponse mis a jour avec son nouveau positionnement transitionnel.
     */
    LeadResponse updateLeadStatus(Long id, enumeration.StatutLead statut);

    /**
     * Supprime definitivement une opportunite commerciale du systeme d'information.
     * <p>
     * Action destructive appliquee en conformite avec les politiques de purge des donnees 
     * et de securite, restreinte aux profils d'administration (ADMIN).
     * </p>
     * * @param id L'identifiant unique du lead a radier de la base de donnees.
     */
    void deleteLead(Long id);
}