package fr.honeygroup.repository;

import fr.honeygroup.bo.DetailsSpecifiques;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Dépôt de données (Repository) Spring Data JPA dédié à la persistance et à la gestion de l'entité {@link DetailsSpecifiques}.
 * <p>
 * Ce composant administre les lignes de spécifications et critères personnalisés (dictionnaire dynamique clé/valeur) 
 * rattachés de manière bidirectionnelle aux dossiers de prospection commerciale (Leads).
 * </p>
 */
@Repository
public interface DetailsSpecifiquesRepository extends JpaRepository<DetailsSpecifiques, Long> {

    /**
     * Récupère l'intégralité des spécifications et notes de formulaires rattachées à un dossier de prospection précis.
     * <p>
     * <strong>Optimisation des requêtes :</strong> Permet d'isoler et de charger à la demande (Lazy Loading) 
     * l'ensemble des réponses complémentaires associées à un lead sans subir le coût d'extraction ou de sérialisation 
     * de l'agrégat parent complet.
     * </p>
     * * @param demandeLeadId Identifiant technique unique du dossier de prospection (Lead) concerné.
     * @return Une liste de {@link DetailsSpecifiques} propres à ce dossier.
     */
    List<DetailsSpecifiques> findByDemandeLeadId(Long demandeLeadId);

    /**
     * Extrait l'ensemble des critères enregistrés dans l'application pour un intitulé de champ de formulaire précis.
     * <p>
     * <strong>Exploitation Datamining & Statistiques :</strong> Utile pour le Staff afin de consolider, analyser ou 
     * filtrer les tendances récurrentes des prospects sur une question sur-mesure (ex: extraire toutes les lignes portant 
     * sur la clé de besoin "technologie_cible" ou "nombre_de_gants").
     * </p>
     * * @param champCle Libellé exact de la clé de formulaire recherchée.
     * @return Une liste de {@link DetailsSpecifiques} partageant cette même clé d'indexation.
     */
    List<DetailsSpecifiques> findByChampCle(String champCle);
}