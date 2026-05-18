package fr.honeygroup.bll.impl;

import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.request.PoleRequest;
import fr.honeygroup.bo.response.PoleResponse;
import fr.honeygroup.mapper.PoleMapper;
import fr.honeygroup.repository.PoleRepository;
import fr.honeygroup.bll.PoleService;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implémentation du service métier gérant les pôles d'activité (Pole) de Honey Group.
 * <p>
 * Cette classe orchestre la récupération des données macroscopiques des pôles (Écotourisme, IT Outsourcing)
 * et centralise la gestion des messages d'erreur et notifications via l'infrastructure 
 * d'internationalisation (i18n) de Spring.
 * </p>
 */
@Service
public class PoleServiceImpl implements PoleService {

    private final PoleRepository poleRepository;
    private final PoleMapper mapper;
    private final MessageSource messageSource;

    /**
     * Constructeur unique permettant l'injection de dépendances native par Spring.
     * * @param poleRepository Le dépôt de données dédié aux pôles.
     * @param mapper Le convertisseur de structures (MapStruct) entre entités et DTOs.
     * @param messageSource Le gestionnaire de bundles de messages pour l'internationalisation.
     */
    public PoleServiceImpl(PoleRepository poleRepository,
                           PoleMapper mapper,
                           MessageSource messageSource) {
        this.poleRepository = poleRepository;
        this.mapper = mapper;
        this.messageSource = messageSource;
    }

    /**
     * Méthode utilitaire d'internationalisation (i18n).
     * <p>
     * Extrait les libellés et messages d'erreur traduits depuis les fichiers de propriétés externes 
     * en se basant sur la langue (Locale) du contexte de la requête de l'utilisateur courant.
     * </p>
     * * @param key Clé unique du message cible définie dans les bundles de propriétés.
     * @return Le message textuel traduit correspondant à la clé.
     */
    private String msg(String key) {
        return messageSource.getMessage(
                key,
                null,
                LocaleContextHolder.getLocale()
        );
    }

    /**
     * Récupère l'intégralité des pôles d'activité enregistrés dans le système.
     * * @return Une liste de {@link PoleResponse} modélisant l'ensemble des pôles.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PoleResponse> getAll() {
        return poleRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    /**
     * Recherche et récupère un pôle d'activité par son identifiant technique unique.
     * * @param id Identifiant technique du pôle à localiser.
     * @return Le {@link PoleResponse} correspondant au pôle trouvé.
     * @throws RuntimeException Si aucun enregistrement ne correspond à l'identifiant fourni (message internationalisé).
     */
    @Override
    @Transactional(readOnly = true)
    public PoleResponse getById(Long id) {
        Pole pole = poleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(msg("pole.notfound")));

        return mapper.toResponse(pole);
    }

    /**
     * Recherche un pôle d'activité en fonction de son libellé nominatif exact.
     * * @param nom Libellé textuel du pôle recherché (ex: "Écotourisme").
     * @return Le {@link PoleResponse} correspondant au pôle localisé.
     * @throws RuntimeException Si le pôle associé au nom fourni reste introuvable (message internationalisé).
     */
    @Override
    @Transactional(readOnly = true)
    public PoleResponse getByNom(String nom) {
        Pole pole = poleRepository.findByNom(nom)
                .orElseThrow(() -> new RuntimeException(msg("pole.nom.notfound")));

        return mapper.toResponse(pole);
    }

    @Override
    @Transactional
    public PoleResponse create(PoleRequest request) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Transactional
    public PoleResponse update(Long id, PoleRequest request) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // TODO Auto-generated method stub
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // TODO Auto-generated method stub
    }
}