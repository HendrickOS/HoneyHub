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
import java.util.List;
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

import fr.honeygroup.bo.Booking;
import fr.honeygroup.bo.Payment;
import fr.honeygroup.bo.Prestation;
import fr.honeygroup.bo.Session;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.request.BookingRequest;
import fr.honeygroup.bo.response.BookingResponse;
import fr.honeygroup.enumeration.Role;
import fr.honeygroup.enumeration.StatutBooking;
import fr.honeygroup.exception.GlobalExceptionHandler.BusinessSecurityException;
import fr.honeygroup.exception.GlobalExceptionHandler.SessionCapacityException;
import fr.honeygroup.mapper.BookingMapper;
import fr.honeygroup.repository.BookingRepository;
import fr.honeygroup.repository.PaymentRepository;
import fr.honeygroup.repository.SessionRepository;
import fr.honeygroup.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests des règles métier de BookingService (BLL)")
class BookingServiceImplTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private SessionRepository sessionRepository;
    @Mock private UserRepository userRepository;
    @Mock private BookingMapper bookingMapper;
    @Mock private PaymentRepository paymentRepository; // Ajout crucial pour éviter les NullPointerExceptions

    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User clientConnecte;
    private User autreClient;
    private User staffConnecte;
    private Session sessionDisponible;
    private BookingRequest requestValide;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);

        clientConnecte = User.builder()
                .id(100L)
                .email("client1@honeygroup.fr")
                .nom("nomClient1")
                .prenom("prenomClient1")
                .role(Role.CLIENT)
                .build();

        autreClient = User.builder()
                .id(999L)
                .email("tiers@honeygroup.fr")
                .role(Role.CLIENT)
                .build();

        staffConnecte = User.builder()
                .id(50L)
                .email("staff@honeygroup.fr")
                .role(Role.MANAGER)
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

    // ============================================================================
    // WORKFLOW DE CRÉATION (creerReservationSandbox)
    // ============================================================================

    @Test
    @DisplayName("Création Sandbox : Succès complet avec calcul financier, jauge et cycle de paiement")
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
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
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
        verify(paymentRepository, times(2)).save(any(Payment.class)); // Appelé deux fois consécutivement dans ton implémentation
    }

    @Test
    @DisplayName("Création Sandbox : Autorisé si un membre du Staff réserve pour un tiers")
    void creerReservationSandbox_SuccesStaffPourTiers() {
        // ARRANGE
        requestValide.setUserId(999L); // Demande pour le tiers
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("staff@honeygroup.fr");
        when(userRepository.findByEmail("staff@honeygroup.fr")).thenReturn(Optional.of(staffConnecte));
        when(bookingMapper.toEntity(requestValide)).thenReturn(new Booking());
        when(userRepository.findById(999L)).thenReturn(Optional.of(autreClient));
        when(sessionRepository.findById(10L)).thenReturn(Optional.of(sessionDisponible));
        when(bookingRepository.save(any(Booking.class))).thenReturn(new Booking());
        when(paymentRepository.save(any(Payment.class))).thenReturn(new Payment());
        when(bookingMapper.toResponse(any(Booking.class))).thenReturn(BookingResponse.builder().build());

        // ACT & ASSERT
        assertNotNull(bookingService.creerReservationSandbox(requestValide));
    }

    @Test
    @DisplayName("Création Sandbox : Échec quand un client tente de réserver pour un tiers (Contre la faille IDOR)")
    void creerReservationSandbox_ErreurIdorTiers() {
        // ARRANGE
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("client1@honeygroup.fr");
        when(userRepository.findByEmail("client1@honeygroup.fr")).thenReturn(Optional.of(clientConnecte));

        BookingRequest requestFraudeuse = new BookingRequest();
        requestFraudeuse.setUserId(999L); 

        // ACT & ASSERT
        assertThrows(BusinessSecurityException.class, () -> {
            bookingService.creerReservationSandbox(requestFraudeuse);
        });

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
        
        sessionDisponible.setNbInscrits(9); // Max 10, demande 2 -> Échec
        when(sessionRepository.findById(10L)).thenReturn(Optional.of(sessionDisponible));

        // ACT & ASSERT
        assertThrows(SessionCapacityException.class, () -> {
            bookingService.creerReservationSandbox(requestValide);
        });

        verify(bookingRepository, never()).save(any());
    }

    // ============================================================================
    // WORKFLOWS DE LECTURE & HISTORIQUES
    // ============================================================================

    @Test
    @DisplayName("Historique Personnel : Extraction exclusive des dossiers de l'utilisateur connecté")
    void getUtilisateurHistoriquePersonnel_Succes() {
        // ARRANGE
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("client1@honeygroup.fr");
        when(userRepository.findByEmail("client1@honeygroup.fr")).thenReturn(Optional.of(clientConnecte));
        
        Booking b = new Booking();
        when(bookingRepository.findByUserIdOrderByDateCreationResaDesc(100L)).thenReturn(List.of(b));
        when(bookingMapper.toResponse(b)).thenReturn(BookingResponse.builder().build());

        // ACT
        List<BookingResponse> result = bookingService.getUtilisateurHistoriquePersonnel();

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).findByUserIdOrderByDateCreationResaDesc(100L);
    }

    @Test
    @DisplayName("Dossier Client pour Staff : Récupération validée si l'ID cible existe")
    void getDossierClientPourStaff_Succes() {
        // ARRANGE
        when(userRepository.existsById(999L)).thenReturn(true);
        Booking b = new Booking();
        when(bookingRepository.findByUserIdOrderByDateCreationResaDesc(999L)).thenReturn(List.of(b));
        when(bookingMapper.toResponse(b)).thenReturn(BookingResponse.builder().build());

        // ACT
        List<BookingResponse> result = bookingService.getDossierClientPourStaff(999L);

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ============================================================================
    // WORKFLOWS D'ANNULATION (demanderAnnulation / approuverAnnulation)
    // ============================================================================

    @Test
    @DisplayName("Demander Annulation : Succès si l'appelant est bien le propriétaire légitime")
    void demanderAnnulation_Succes() {
        // ARRANGE
        Booking bookingValide = Booking.builder()
                .id(200L)
                .user(clientConnecte)
                .statut(StatutBooking.CONFIRME)
                .build();

        when(bookingRepository.findById(200L)).thenReturn(Optional.of(bookingValide));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("client1@honeygroup.fr");

        // ACT
        bookingService.demanderAnnulation(200L);

        // ASSERT
        assertEquals(StatutBooking.DEMANDE_ANNULATION, bookingValide.getStatut());
        verify(bookingRepository, times(1)).save(bookingValide);
    }

    @Test
    @DisplayName("Demander Annulation : Échec (IDOR) si l'appelant n'est pas le propriétaire du dossier")
    void demanderAnnulation_ErreurIdor() {
        // ARRANGE
        Booking bookingTiers = Booking.builder()
                .id(200L)
                .user(autreClient) // Appartient à ID 999
                .statut(StatutBooking.CONFIRME)
                .build();

        when(bookingRepository.findById(200L)).thenReturn(Optional.of(bookingTiers));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("client1@honeygroup.fr"); // ID 100

        // ACT & ASSERT
        assertThrows(BusinessSecurityException.class, () -> {
            bookingService.demanderAnnulation(200L);
        });
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