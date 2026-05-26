package fr.honeygroup.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import fr.honeygroup.bo.Prestation;
import fr.honeygroup.bo.Session;
import fr.honeygroup.bo.request.SessionRequest;
import fr.honeygroup.bo.response.SessionResponse;
import fr.honeygroup.enumeration.StatutSession;

@DisplayName("Tests de mapping pour SessionMapper")
class SessionMapperTest {

    private final SessionMapper mapper = Mappers.getMapper(SessionMapper.class);

    @Test
    @DisplayName("Mapping : Entité Session vers SessionResponse (Flattening)")
    void sessionToResponse_MappingValide() {
        // 1. Préparation de l'entité source
        Prestation prestation = new Prestation();
        prestation.setId(101L);
        prestation.setTitreService("Safari Découverte");

        Session session = new Session();
        session.setId(1L);
        session.setPrestation(prestation);
        session.setStatutSession(StatutSession.OUVERT);
        session.setNbInscrits(5);
        session.setCapaciteMax(20);

        // 2. Mapping
        SessionResponse response = mapper.toResponse(session);

        // 3. Vérifications
        assertNotNull(response);
        assertEquals("Safari Découverte", response.getPrestationNom()); // Test du flattening
        assertEquals(101L, response.getPrestationId());
        assertEquals(5, response.getParticipantsActuels());
        assertEquals(StatutSession.OUVERT, response.getStatut());
    }

    @Test
    @DisplayName("Mapping : SessionRequest vers Entité Session")
    void sessionRequestToEntity_MappingValide() {
        SessionRequest request = new SessionRequest();
        request.setStatut(StatutSession.OUVERT);
        request.setCapaciteMax(15);

        Session session = mapper.toEntity(request);

        assertNotNull(session);
        assertEquals(StatutSession.OUVERT, session.getStatutSession());
        assertEquals(15, session.getCapaciteMax());
        assertNull(session.getPrestation(), "La prestation doit être ignorée lors du mapping du Request");
    }
}