package fr.honeygroup.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests de conversion JPA pour HashMapConverter")
class HashMapConverterTest {

    private final HashMapConverter converter = new HashMapConverter();

    @Test
    @DisplayName("Conversion : Map vers JSON (Database Column)")
    void converter_ToDatabaseColumn_Success() {
        Map<String, Object> data = Map.of(
            "difficulte", "Moyenne",
            "equipement", "Sac de couchage"
        );

        String json = converter.convertToDatabaseColumn(data);

        assertNotNull(json);
        assertTrue(json.contains("\"difficulte\":\"Moyenne\""));
        assertTrue(json.contains("\"equipement\":\"Sac de couchage\""));
    }

    @Test
    @DisplayName("Conversion : JSON vers Map (Entity Attribute)")
    void converter_ToEntityAttribute_Success() {
        String json = "{\"difficulte\":\"Moyenne\",\"equipement\":\"Sac de couchage\"}";

        Map<String, Object> data = converter.convertToEntityAttribute(json);

        assertNotNull(data);
        assertEquals("Moyenne", data.get("difficulte"));
        assertEquals("Sac de couchage", data.get("equipement"));
    }

    @Test
    @DisplayName("Gestion des erreurs : Exception sur JSON invalide")
    void converter_ErrorHandling() {
        String invalidJson = "{ invalid json }";
        
        assertThrows(IllegalArgumentException.class, () -> {
            converter.convertToEntityAttribute(invalidJson);
        }, "Une erreur devrait être levée pour un JSON mal formé");
    }
}