package fr.honeygroup.bo.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objet de transfert de données (DTO Request) utilisé lors de la soumission 
 * d'une nouvelle preuve de paiement par le client.
 * <p>
 * Ce DTO centralise les informations nécessaires à la création d'une transaction 
 * en attente de vérification comptable.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    /**
     * Identifiant technique de la réservation (Booking) à laquelle le paiement est rattaché.
     */
    private Long bookingId;

    /**
     * Montant total déclaré par le client pour cette transaction.
     */
    private BigDecimal montantPaye;

    /**
     * Référence ou numéro de reçu fourni par l'opérateur financier (ex: identifiant de transfert).
     */
    private String transactionId;

    /**
     * Canal de paiement utilisé par le client (ex: VIREMENT, MOBILE_MONEY).
     */
    private String methode;

    /**
     * URL ou chemin vers le justificatif numérique (PDF/Image) téléversé sur le stockage sécurisé.
     */
    private String preuveUrl;
}