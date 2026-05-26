package fr.honeygroup.enumeration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests de conformité de l'énumération TypePayment")
class TypePaymentTest {

    @Test
    @DisplayName("Conformité : Vérification de la complétude des canaux de paiement")
    void typePayment_EnumConstantes_DoiventEtrePresentes() {
        // Vérification de la présence des 4 modes de paiement officiels
        assertDoesNotThrow(() -> TypePayment.valueOf("VIREMENT_BANCAIRE"));
        assertDoesNotThrow(() -> TypePayment.valueOf("MOBILE_MONEY"));
        assertDoesNotThrow(() -> TypePayment.valueOf("PAYPAL"));
        assertDoesNotThrow(() -> TypePayment.valueOf("CARTE_BANCAIRE"));
        
        // Vérification stricte du nombre de canaux
        assertEquals(4, TypePayment.values().length);
    }

    @Test
    @DisplayName("Identité : Vérification des noms des constantes")
    void typePayment_Noms_DoiventEtreConformes() {
        assertEquals("VIREMENT_BANCAIRE", TypePayment.VIREMENT_BANCAIRE.name());
        assertEquals("MOBILE_MONEY", TypePayment.MOBILE_MONEY.name());
        assertEquals("PAYPAL", TypePayment.PAYPAL.name());
        assertEquals("CARTE_BANCAIRE", TypePayment.CARTE_BANCAIRE.name());
    }
}