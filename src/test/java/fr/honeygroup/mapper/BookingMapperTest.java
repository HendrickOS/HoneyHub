package fr.honeygroup.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import fr.honeygroup.bo.Booking;
import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.Prestation;
import fr.honeygroup.bo.Session;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.response.BookingResponse;

@DisplayName("Tests de mapping pour BookingMapper")
class BookingMapperTest {

    private final BookingMapper mapper = Mappers.getMapper(BookingMapper.class);

    @Test
    @DisplayName("Mapping : Entité Booking vers BookingResponse avec aplatissement")
    void bookingToResponse_MappingValide() {
        // 1. Préparation de l'entité source (Graph)
        User user = new User();
        user.setNom("Dupont");
        user.setPrenom("Jean");

        Pole pole = new Pole();
        pole.setNom("Écotourisme");

        Prestation prestation = new Prestation();
        prestation.setTitreService("Trek Atlas");
        prestation.setPole(pole);

        Session session = new Session();
        session.setPrestation(prestation);
        session.setDateDebut(LocalDateTime.now());

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setSession(session);
        booking.setNbPlaces(2);
        booking.setPayments(Collections.emptyList()); // Pas de paiements pour le test

        // 2. Exécution du mapping
        BookingResponse response = mapper.toResponse(booking);

        // 3. Vérifications (Aplatissement)
        assertNotNull(response);
        assertEquals("DUPONT Jean", response.getUserNomComplet()); // Test de mapNomComplet
        assertEquals("Écotourisme", response.getPoleNom());
        assertEquals("Trek Atlas", response.getPrestationTitre());
        assertEquals(2, response.getNbPersonnes());
    }
}