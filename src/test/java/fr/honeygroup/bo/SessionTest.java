package fr.honeygroup.bo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.honeygroup.enumeration.StatutSession;

@DisplayName("Tests unitaires de l'entité Session (Planification)")
class SessionTest {

    private Prestation prestationDeTest;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;

    @BeforeEach
    void setUp() {
        // Initialisation d'une prestation factice et des plages temporelles de test
        prestationDeTest = new Prestation();
        dateDebut = LocalDateTime.now().plusDays(10);
        dateFin = LocalDateTime.now().plusDays(20);
    }

    @Test
    @DisplayName("Vérification des valeurs initiales et des états par défaut d'une Session")
    void initialisation_DevraitAvoirLesValeursParDefaut() {
        // ARRANGE & ACT
        Session session = new Session();

        // ASSERT
        assertEquals(0, session.getNbInscrits(), 
                "Le compteur initial d'inscrits doit être égal à zéro.");
        assertEquals(StatutSession.OUVERT, session.getStatutSession(), 
                "Une nouvelle session doit s'instancier avec le statut opérationnel 'OUVERT'.");
    }

    @Test
    @DisplayName("Vérification du fonctionnement du Builder Lombok sur l'ensemble des champs d'une Session")
    void builder_DevraitConstruireLObjetCorrectement() {
        // ARRANGE & ACT
        Session session = Session.builder()
                .id(7L)
                .dateDebut(dateDebut)
                .dateFin(dateFin)
                .capaciteMax(15)
                .prestation(prestationDeTest)
                .build();

        // ASSERT
        assertNotNull(session);
        assertEquals(7L, session.getId());
        assertEquals(dateDebut, session.getDateDebut());
        assertEquals(dateFin, session.getDateFin());
        assertEquals(15, session.getCapaciteMax());
        assertEquals(prestationDeTest, session.getPrestation());
        
        // Validation que @Builder.Default préserve bien les valeurs par défaut
        assertEquals(0, session.getNbInscrits(), 
                "Le builder doit conserver la valeur par défaut de 0 inscrit si non spécifiée.");
        assertEquals(StatutSession.OUVERT, session.getStatutSession(), 
                "Le builder doit conserver la valeur par défaut 'OUVERT' si non spécifiée.");
    }

    @Test
    @DisplayName("Vérification que le Builder écrase les valeurs par défaut si elles sont fournies explicitement")
    void builder_DevraitPrendreLesValeursExplicites_QuandEllesSontFournies() {
        // ARRANGE & ACT
        Session session = Session.builder()
                .capaciteMax(12)
                .nbInscrits(12)
                .statutSession(StatutSession.COMPLET)
                .build();

        // ASSERT
        assertEquals(12, session.getNbInscrits(), "Le builder doit écraser la valeur par défaut par 12.");
        assertEquals(StatutSession.COMPLET, session.getStatutSession(), "Le builder doit écraser le statut par COMPLET.");
    }
}