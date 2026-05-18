package fr.honeygroup.repository;

import fr.honeygroup.bo.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Dépôt de données (Repository) Spring Data JPA dédié à la persistance et à la gestion de l'entité {@link Payment}.
 * <p>
 * Ce composant centralise le requêtage et le contrôle des flux financiers du pôle Écotourisme, 
 * permettant le suivi des transactions monétaires et l'audit des justificatifs de versements.
 * </p>
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    /**
     * Recherche une transaction financière à partir de sa référence unique émise par l'opérateur externe.
     * <p>
     * <strong>Sécurité anti-fraude :</strong> Cette méthode permet d'intercepter et de bloquer les attaques par Replay 
     * ou les doublons d'enregistrements en s'assurant qu'un identifiant Stripe, un virement ou un reçu Mobile Money 
     * n'a pas déjà été comptabilisé dans le système.
     * </p>
     * * @param transactionId Référence unique de la transaction fournie par la passerelle de paiement ou le reçu.
     * @return Un {@link Optional} englobant le paiement s'il existe déjà en base de données.
     */
    Optional<Payment> findByTransactionId(String transactionId);
    
    /**
     * Extrait l'historique exhaustif des lignes de règlements et acomptes imputés à un dossier de réservation.
     * <p>
     * Cette méthode permet à la couche métier (BLL) de calculer le reste à payer d'un dossier 
     * et alimente de manière transparente les listes de transactions affichées sur l'espace client.
     * </p>
     * * @param bookingId Identifiant technique unique du dossier de réservation parent.
     * @return Une liste de {@link Payment} rattachés contractuellement à ce dossier.
     */
    List<Payment> findByBookingId(Long bookingId);
}