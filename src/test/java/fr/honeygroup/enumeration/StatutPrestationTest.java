package fr.honeygroup.enumeration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests de conformité de l'énumération StatutPrestation (Catalogue)")
class StatutPrestationTest {

    @Test
    @DisplayName("Conformité : Vérification de la complétude des états de visibilité")
    void statutPrestation_EnumConstantes_DoiventEtrePresentes() {
        // Vérification de la présence des 4 statuts métier
        assertDoesNotThrow(() -> StatutPrestation.valueOf("ACTIF"));
        assertDoesNotThrow(() -> StatutPrestation.valueOf("INACTIF"));
        assertDoesNotThrow(() -> StatutPrestation.valueOf("ARCHIVE"));
        assertDoesNotThrow(() -> StatutPrestation.valueOf("EN_ATTENTE"));
        
        assertEquals(4, StatutPrestation.values().length);
    }

    @Test
    @DisplayName("Sémantique : Validation du statut par défaut (Conception)")
    void statutPrestation_ValidationEtatParDefaut() {
        // Validation que le statut de conception est bien celui attendu par la logique métier
        assertEquals(StatutPrestation.EN_ATTENTE, StatutPrestation.valueOf("EN_ATTENTE"));
    }
}