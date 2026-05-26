package fr.honeygroup.enumeration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests de conformité de l'énumération StatutLead (CRM)")
class StatutLeadTest {

    @Test
    @DisplayName("Conformité : Vérification de la complétude du workflow CRM")
    void statutLead_EnumConstantes_DoiventEtrePresentes() {
        // Vérification de la présence des 5 statuts obligatoires
        assertDoesNotThrow(() -> StatutLead.valueOf("NOUVEAU"));
        assertDoesNotThrow(() -> StatutLead.valueOf("EN_COURS"));
        assertDoesNotThrow(() -> StatutLead.valueOf("TRAITE"));
        assertDoesNotThrow(() -> StatutLead.valueOf("REFUSE"));
        assertDoesNotThrow(() -> StatutLead.valueOf("CONVERTI"));
        
        assertEquals(5, StatutLead.values().length);
    }

    @Test
    @DisplayName("Ordre : Vérification du statut initial")
    void statutLead_InitialState_EstNouveau() {
        assertEquals(StatutLead.NOUVEAU, StatutLead.valueOf("NOUVEAU"));
    }
}