package fr.honeygroup.bo.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import fr.honeygroup.enumeration.StatutPayment;
import fr.honeygroup.enumeration.TypePayment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objet de transfert de données (DTO Response) modélisant la réponse structurée 
 * d'un règlement financier (Payment) associé à un dossier de réservation.
 * <p>
 * Cette classe permet d'exposer l'état d'avancement d'une transaction comptable ou du contrôle 
 * d'une pièce justificative (virement bancaire ou Mobile Money), offrant une visibilité en temps réel 
 * à l'utilisateur et aux gestionnaires de Honey Group.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    /**
     * Identifiant technique unique du paiement en base de données.
     */
    private Long id;

    /**
     * Canal d'acquisition monétaire utilisé pour la transaction (ex: STRIPE, VIREMENT, MOBILE_MONEY).
     */
    private TypePayment methode;

    /**
     * Référence ou numéro de reçu unique émis par l'opérateur financier externe.
     */
    private String transactionId;

    /**
     * Montant exact perçu pour cette ligne de transaction financière.
     */
    private BigDecimal montantPaye;

    /**
     * Horodatage système marquant la soumission ou l'enregistrement du règlement.
     */
    private LocalDateTime datePaiement;

    /**
     * État du traitement comptable de l'opération.
     * Utilise directement l'énumération {@link StatutPayment} pour garantir la cohérence 
     * entre la BLL et les couches d'exposition.
     */
    private StatutPayment statutPaiement;

    /**
     * URL ou chemin d'accès sécurisé vers le fichier justificatif téléversé (PDF ou image du reçu).
     * Permet au personnel d'afficher et d'auditer visuellement la validité du transfert.
     */
    private String preuveUrl;
}