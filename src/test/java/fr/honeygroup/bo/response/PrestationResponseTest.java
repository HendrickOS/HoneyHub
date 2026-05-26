package fr.honeygroup.bo.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests de structure du DTO PrestationResponse")
class PrestationResponseTest {

    @Test
    @DisplayName("Polymorphisme : Construction d'une réponse de type CIRCUIT")
    void prestationResponse_Circuit_BuilderSucces() {
        PrestationResponse response = PrestationResponse.builder()
                .id(1L)
                .type("CIRCUIT")
                .titreService("Safari Découverte")
                .itineraire("Antananarivo -> Majunga")
                .duree("7 jours / 6 nuits")
                .build();

        assertEquals("CIRCUIT", response.getType());
        assertEquals("Safari Découverte", response.getTitreService());
        assertEquals("7 jours / 6 nuits", response.getDuree());
        assertNull(response.getLangue(), "Les champs de cours de langue doivent être nuls");
    }

    @Test
    @DisplayName("Polymorphisme : Construction d'une réponse de type COURS_LANGUE")
    void prestationResponse_CoursLangue_BuilderSucces() {
        PrestationResponse response = PrestationResponse.builder()
                .id(2L)
                .type("COURS_LANGUE")
                .langue("Japonais")
                .niveau("N4")
                .descriptifProgramme("Apprentissage des bases.")
                .build();

        assertEquals("COURS_LANGUE", response.getType());
        assertEquals("Japonais", response.getLangue());
        assertEquals("N4", response.getNiveau());
        assertNull(response.getItineraire(), "Les champs de circuit doivent être nuls");
    }

    @Test
    @DisplayName("Metadata : Vérification de la persistance des données dynamiques")
    void prestationResponse_Metadata_Fonctionnel() {
        Map<String, Object> meta = Map.of("difficulte", "Moyenne");
        PrestationResponse response = PrestationResponse.builder()
                .metadata(meta)
                .build();

        assertEquals(meta, response.getMetadata());
        assertEquals("Moyenne", response.getMetadata().get("difficulte"));
    }
}