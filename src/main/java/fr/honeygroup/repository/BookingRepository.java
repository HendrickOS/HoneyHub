package fr.honeygroup.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.honeygroup.bo.Booking;
import fr.honeygroup.enumeration.StatutBooking;

/**
 * Dépôt de données (Repository) Spring Data JPA dédié à la persistance et à la gestion de l'entité {@link Booking}.
 * <p>
 * Ce composant constitue le pivot central du pôle Écotourisme. Il supervise l'accès aux dossiers 
 * de réservation, leur traçabilité chronologique par compte utilisateur, le filtrage par statut 
 * de cycle de vie et l'audit opérationnel des participants par session temporelle fixe.
 * </p>
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    /**
     * Extrait l'historique complet des réservations d'un utilisateur, ordonné par ordre chronologique inverse.
     * <p>
     * <strong>Sécurité & Affichage :</strong> Alimente directement la vue de l'historique personnel du client. 
     * Le tri descendant ({@code OrderByDateCreationResaDesc}) est exécuté nativement au niveau de MariaDB, 
     * garantissant que les dossiers les plus récents s'affichent instantanément en tête d'interface.
     * </p>
     * * @param userId Identifiant technique unique de l'utilisateur connecté.
     * @return Une liste de {@link Booking} triée de la plus récente à la plus ancienne.
     */
    List<Booking> findByUserIdOrderByDateCreationResaDesc(Long userId);
    
    /**
     * Filtre et extrait l'ensemble des dossiers de réservation selon leur état d'avancement métier.
     * <p>
     * <strong>Pilotage Administrateur :</strong> Fournit au personnel de gestion et aux modules d'arrière-guichet 
     * les listes segmentées nécessaires aux workflows critiques (ex: traitement des flux {@code EN_ATTENTE_PAIEMENT} 
     * ou arbitrage des alertes de type {@code DEMANDE_ANNULATION}).
     * </p>
     * * @param statut Le libellé textuel du statut recherché (issu de l'énumération métier).
     * @return Une liste de {@link Booking} partageant cet état.
     */
    List<Booking> findByStatut(StatutBooking statut);

    /**
     * Récupère l'intégralité des réservations contractées sur une session temporelle fixe donnée.
     * <p>
     * <strong>Cohérence Logistique :</strong> Cette méthode permet à la couche métier (BLL) de calculer 
     * précisément le volume d'inscriptions réelles ou de lister le registre des passagers et participants 
     * attendus au départ d'un circuit écotouristique spécifique.
     * </p>
     * * @param sessionId Identifiant technique unique de la session ciblée.
     * @return Une liste de {@link Booking} rattachées à cette session.
     */
    List<Booking> findBySessionId(Long sessionId);
}