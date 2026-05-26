package fr.honeygroup.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import fr.honeygroup.bo.Circuit;
import fr.honeygroup.bo.CoursLangue;
import fr.honeygroup.bo.Prestation;
import fr.honeygroup.bo.response.PrestationResponse;

@DisplayName("Tests de mapping polymorphique pour PrestationMapper")
class PrestationMapperTest {

    private final PrestationMapper mapper = Mappers.getMapper(PrestationMapper.class);

    @Test
    @DisplayName("Mapping : Polymorphisme - Circuit vers PrestationResponse")
    void toGenericResponse_Circuit_Success() {
        Circuit circuit = new Circuit();
        circuit.setTitreService("Safari");
        circuit.setItineraire("Nord -> Sud");
        
        PrestationResponse response = mapper.toGenericResponse(circuit);

        assertEquals("CIRCUIT", response.getType());
        assertEquals("Safari", response.getTitreService());
        assertEquals("Nord -> Sud", response.getItineraire());
    }

    @Test
    @DisplayName("Mapping : Polymorphisme - CoursLangue vers PrestationResponse")
    void toGenericResponse_CoursLangue_Success() {
        CoursLangue cours = new CoursLangue();
        cours.setTitreService("Espagnol Intensif");
        cours.setLangue("Espagnol");
        
        PrestationResponse response = mapper.toGenericResponse(cours);

        assertEquals("COURS_LANGUE", response.getType());
        assertEquals("Espagnol Intensif", response.getTitreService());
        assertEquals("Espagnol", response.getLangue());
    }

    @Test
    @DisplayName("Mapping : Polymorphisme - Prestation générique")
    void toGenericResponse_Generique_Success() {
        Prestation prestation = new Prestation();
        prestation.setTitreService("Simple Réservation");
        
        PrestationResponse response = mapper.toGenericResponse(prestation);

        assertEquals("GENERIQUE", response.getType());
        assertEquals("Simple Réservation", response.getTitreService());
    }
}