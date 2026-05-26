package fr.honeygroup.enumeration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Tests de l'automate d'états StatutSession")
class StatutSessionTest {

    @ParameterizedTest
    @CsvSource({
        "OUVERT, COMPLET, true",
        "OUVERT, ANNULE, true",
        "OUVERT, EN_COURS, true",
        "OUVERT, CLOTURE, false",
        "COMPLET, OUVERT, true",
        "COMPLET, EN_COURS, true",
        "EN_COURS, CLOTURE, true",
        "EN_COURS, ANNULE, false",
        "CLOTURE, OUVERT, false",
        "ANNULE, OUVERT, false"
    })
    @DisplayName("Transitions : Vérification de la validité des changements d'état")
    void peutBasculerVers_DevraitRetournerLeBonResultat(StatutSession actuel, StatutSession cible, boolean attendu) {
        assertEquals(attendu, actuel.peutBasculerVers(cible), 
            String.format("La transition de %s vers %s devrait être %b", actuel, cible, attendu));
    }

    @Test
    @DisplayName("Consistance : Vérification de la présence de tous les états")
    void enum_DoitAvoirLeNombreExactDeStatuts() {
        assertEquals(5, StatutSession.values().length);
    }
}