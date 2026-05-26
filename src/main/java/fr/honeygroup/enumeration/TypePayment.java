package fr.honeygroup.enumeration;

/**
 * Énumération listant les canaux de règlement acceptés par Honey Group.
 * <p>
 * Ces méthodes de paiement sont utilisées pour catégoriser les transactions 
 * financières et faciliter la réconciliation comptable automatique.
 * </p>
 */
public enum TypePayment {

    /**
     * Virement bancaire classique (transfert de compte à compte).
     */
    VIREMENT_BANCAIRE,

    /**
     * Paiement via les solutions de Mobile Money (Mvola, Orange Money, Airtel Money).
     */
    MOBILE_MONEY,

    /**
     * Paiement en ligne sécurisé via la plateforme PayPal.
     */
    PAYPAL,

    /**
     * Paiement direct par carte bancaire (Stripe ou passerelle équivalente).
     */
    CARTE_BANCAIRE
}