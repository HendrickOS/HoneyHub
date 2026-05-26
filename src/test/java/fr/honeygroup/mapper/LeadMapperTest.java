package fr.honeygroup.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import fr.honeygroup.bo.DemandeLead;
import fr.honeygroup.bo.DetailsSpecifiques;
import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.response.LeadResponse;

@DisplayName("Tests de mapping pour LeadMapper")
class LeadMapperTest {

    private final LeadMapper mapper = Mappers.getMapper(LeadMapper.class);

    @Test
    @DisplayName("Mapping : Transformation complète d'un Lead (Utilisateur enregistré)")
    void leadToResponse_MappingUtilisateur_Succes() {
        // 1. Préparation des données
        User user = new User();
        user.setId(10L);
        user.setNom("MARTIN");
        user.setPrenom("Alice");

        Pole pole = new Pole();
        pole.setId(1L);
        pole.setNom("IT Outsourcing");

     // Remplacement dans ton test LeadMapperTest.java
        DetailsSpecifiques detail1 = new DetailsSpecifiques();
        detail1.setChampCle("Technologie");
        detail1.setValeur("Java");

        DetailsSpecifiques detail2 = new DetailsSpecifiques();
        detail2.setChampCle("Budget");
        detail2.setValeur("10k");

        DemandeLead lead = new DemandeLead();
        lead.setId(100L);
        lead.setUser(user);
        lead.setPole(pole);
        lead.setSpecificDetails(List.of(detail1, detail2));

        // 2. Mapping
        LeadResponse response = mapper.toResponse(lead);

        // 3. Vérifications
        assertNotNull(response);
        assertEquals("MARTIN Alice", response.getUserNomComplet());
        assertEquals("IT Outsourcing", response.getPoleNom());
        assertEquals(2, response.getSpecificDetails().size());
        assertEquals("Java", response.getSpecificDetails().get("Technologie"));
    }

    @Test
    @DisplayName("Mapping : Gestion des visiteurs anonymes (sans User)")
    void leadToResponse_MappingAnonyme_Succes() {
        DemandeLead lead = new DemandeLead();
        lead.setNomContact("Visiteur Inconnu");
        lead.setEmailContact("contact@test.com");

        LeadResponse response = mapper.toResponse(lead);

        assertEquals("Visiteur Inconnu", response.getNomContact());
        assertEquals("contact@test.com", response.getEmailContact());
        assertNull(response.getUserNomComplet());
    }
}