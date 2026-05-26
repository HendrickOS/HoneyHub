package fr.honeygroup.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

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
import fr.honeygroup.bo.User;
import fr.honeygroup.repository.BookingRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service de sécurité métier (RBAC/Ownership)")
class SecurityServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private SecurityService securityService;

    @BeforeEach
    void setupSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Propriétaire : Autorisé quand le booking appartient à l'utilisateur connecté")
    void isOwnerOfBooking_ShouldReturnTrue_WhenOwner() {
        String email = "client@honeygroup.fr";
        User user = new User();
        user.setEmail(email);

        Booking booking = new Booking();
        booking.setUser(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertTrue(securityService.isOwnerOfBooking(1L));
    }

    @Test
    @DisplayName("Propriétaire : Refusé quand le booking appartient à un autre")
    void isOwnerOfBooking_ShouldReturnFalse_WhenNotOwner() {
        User owner = new User();
        owner.setEmail("owner@honeygroup.fr");
        Booking booking = new Booking();
        booking.setUser(owner);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("intruder@honeygroup.fr");
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertFalse(securityService.isOwnerOfBooking(1L));
    }
}