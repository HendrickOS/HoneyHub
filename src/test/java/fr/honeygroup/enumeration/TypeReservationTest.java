package fr.honeygroup.enumeration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests de conformité de l'énumération TypeReservation")
class TypeReservationTest {

    @Test
    @DisplayName("Conformité : Vérification de la structure des types de réservation")
    void typeReservation_EnumConstantes_DoiventEtrePresentes() {
        // Vérification de la présence des 2 types de flux
        assertDoesNotThrow(() -> TypeReservation.valueOf("SESSION"));
        assertDoesNotThrow(() -> TypeReservation.valueOf("SUR_MESURE"));
        
        // Vérification stricte du nombre de types
        assertEquals(2, TypeReservation.values().length);
    }

    @Test
    @DisplayName("Sémantique : Vérification des types de flux métiers")
    void typeReservation_Nommage_DoitEtreConforme() {
        assertEquals("SESSION", TypeReservation.SESSION.name());
        assertEquals("SUR_MESURE", TypeReservation.SUR_MESURE.name());
    }
}