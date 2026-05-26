package fr.honeygroup.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.request.PoleRequest;
import fr.honeygroup.bo.response.PoleResponse;

@DisplayName("Tests de mapping pour PoleMapper")
class PoleMapperTest {

    private final PoleMapper mapper = Mappers.getMapper(PoleMapper.class);

    @Test
    @DisplayName("Mapping : Entité Pole vers PoleResponse")
    void poleToResponse_MappingValide() {
        Pole pole = new Pole();
        pole.setId(1L);
        pole.setNom("Écotourisme");
        pole.setDescription("Découverte de la biodiversité.");

        PoleResponse response = mapper.toResponse(pole);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Écotourisme", response.getNom());
        assertEquals("Découverte de la biodiversité.", response.getDescription());
    }

    @Test
    @DisplayName("Mapping : PoleRequest vers Entité Pole")
    void poleRequestToEntity_MappingValide() {
        PoleRequest request = new PoleRequest();
        request.setNom("IT Outsourcing");
        request.setDescription("Externalisation informatique.");

        Pole pole = mapper.toEntity(request);

        assertNotNull(pole);
        assertEquals("IT Outsourcing", pole.getNom());
        assertEquals("Externalisation informatique.", pole.getDescription());
        assertNull(pole.getId(), "L'ID doit être nul lors d'une création via le DTO Request");
    }
}