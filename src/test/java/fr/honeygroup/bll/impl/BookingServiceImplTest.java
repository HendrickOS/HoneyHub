package fr.honeygroup.bll.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import enumeration.Role;
import enumeration.StatutBooking;
import fr.honeygroup.bo.Booking;
import fr.honeygroup.bo.Prestation;
import fr.honeygroup.bo.Session;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.request.BookingRequest;
import fr.honeygroup.bo.response.BookingResponse;
import fr.honeygroup.exception.GlobalExceptionHandler.BusinessSecurityException;
import fr.honeygroup.exception.GlobalExceptionHandler.SessionCapacityException;
import fr.honeygroup.mapper.BookingMapper;
import fr.honeygroup.repository.BookingRepository;
import fr.honeygroup.repository.SessionRepository;
import fr.honeygroup.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests des règles métier de BookingService (BLL)")
class BookingServiceImplTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private SessionRepository sessionRepository;
    @Mock private UserRepository userRepository;
    @Mock private BookingMapper bookingMapper;

    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User clientConnecte;
    private Session sessionDisponible;
    private BookingRequest requestValide;

    @BeforeEach
    void setUp() {
        // Mocking du contexte de sécurité global pour simuler un utilisateur authentifié
        SecurityContextHolder.setContext(securityContext);

        // Initialisation des données avec nomenclature professionnelle standardisée
        clientConnecte = User.builder()
                .id(100L)
                .email("client1@honeygroup.fr")
                .nom("nomClient1")
                .prenom("prenomClient1")
                .role(Role.CLIENT)
                .build();
        
        Prestation prestation = new Prestation();
        prestation.setPrixBase(200.0);
        
        sessionDisponible = Session.builder()
                .id(10L)
                .capaciteMax(10)
                .nbInscrits(5)
                .prestation(prestation)
                .build();

        requestValide = new BookingRequest();
        requestValide.setUserId(100L);
        requestValide.setSessionId(10L);
        requestValide.setNbPersonnes(2);
    }

    @Test
    @DisplayName("Création Sandbox : Succès complet avec calcul financier et incrémentation de jauge")
    void creerReservationSandbox_Succes() {
        // ARRANGE
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("client1@honeygroup.fr");
        when(userRepository.findByEmail("client1@honeygroup.fr")).thenReturn(Optional.of(clientConnecte));
        
        Booking bookingInitial = new Booking();
        when(bookingMapper.toEntity(requestValide)).thenReturn(bookingInitial);
        
        when(userRepository.findById(100L)).thenReturn(Optional.of(clientConnecte));
        when(sessionRepository.findById(10L)).thenReturn(Optional.of(sessionDisponible));
        
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        BookingResponse expectedResponse = BookingResponse.builder().build();
        when(bookingMapper.toResponse(any(Booking.class))).thenReturn(expectedResponse);

        // ACT
        BookingResponse actualResponse = bookingService.creerReservationSandbox(requestValide);

        // ASSERT
        assertNotNull(actualResponse);
        assertEquals(7, sessionDisponible.getNbInscrits(), "La jauge d'inscrits de la session doit passer de 5 à 7.");
        assertEquals(new BigDecimal("400.0"), bookingInitial.getMontantTotal(), "Le prix calculé doit être de 200.0 * 2 = 400.0.");
        assertEquals(StatutBooking.EN_ATTENTE_PAIEMENT, bookingInitial.getStatut(), "Le dossier doit s'initialiser au statut d'attente de paiement.");
        
        verify(bookingRepository, times(1)).save(bookingInitial);
    }

    @Test
    @DisplayName("Création Sandbox : Échec quand un client tente de réserver pour un tiers (Contre la faille IDOR)")
    void creerReservationSandbox_ErreurIdorTiers() {
        // ARRANGE
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("client1@honeygroup.fr");
        when(userRepository.findByEmail("client1@honeygroup.fr")).thenReturn(Optional.of(clientConnecte));

        // Le client1 (ID 100) tente frauduleusement de passer une requête pour un compte tiers (ID 999)
        BookingRequest requestFraudeuse = new BookingRequest();
        requestFraudeuse.setUserId(999L); 

        // ACT & ASSERT
        assertThrows(BusinessSecurityException.class, () -> {
            bookingService.creerReservationSandbox(requestFraudeuse);
        }, "Le système doit bloquer la requête en levant une exception de sécurité.");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Création Sandbox : Échec quand la jauge de capacité maximale de la session est dépassée")
    void creerReservationSandbox_ErreurCapaciteDepassee() {
        // ARRANGE
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("client1@honeygroup.fr");
        when(userRepository.findByEmail("client1@honeygroup.fr")).thenReturn(Optional.of(clientConnecte));
        when(bookingMapper.toEntity(requestValide)).thenReturn(new Booking());
        when(userRepository.findById(100L)).thenReturn(Optional.of(clientConnecte));
        
        // Session saturée à 9 inscrits pour 10 places max. Les 2 places demandées doivent faire échouer le traitement.
        sessionDisponible.setNbInscrits(9);
        when(sessionRepository.findById(10L)).thenReturn(Optional.of(sessionDisponible));

        // ACT & ASSERT
        assertThrows(SessionCapacityException.class, () -> {
            bookingService.creerReservationSandbox(requestValide);
        }, "L'écriture doit avorter si la capacité de places est insuffisante.");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Approuver Annulation : Succès, bascule du statut et restitution des places de la session")
    void approuverAnnulation_Succes() {
        // ARRANGE
        Booking bookingAnnule = Booking.builder()
                .id(50L)
                .session(sessionDisponible)
                .nbPlaces(3)
                .statut(StatutBooking.DEMANDE_ANNULATION)
                .build();
        
        when(bookingRepository.findById(50L)).thenReturn(Optional.of(bookingAnnule));

        // ACT
        bookingService.approuverAnnulation(50L);

        // ASSERT
        assertEquals(StatutBooking.ANNULE, bookingAnnule.getStatut(), "Le statut du dossier doit muter à ANNULE.");
        assertEquals(2, sessionDisponible.getNbInscrits(), "Les 3 places libérées doivent être soustraites de la session (5 - 3 = 2).");
        verify(bookingRepository, times(1)).save(bookingAnnule);
    }
}