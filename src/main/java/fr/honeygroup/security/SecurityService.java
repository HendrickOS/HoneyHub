package fr.honeygroup.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import fr.honeygroup.bo.Booking;
import fr.honeygroup.repository.BookingRepository;
import lombok.RequiredArgsConstructor;

@Service("securityService") // Le nom utilisé dans @PreAuthorize
@RequiredArgsConstructor
public class SecurityService {

    private final BookingRepository bookingRepository;

    public boolean isOwnerOfBooking(Long bookingId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName(); // L'email de l'utilisateur connecté

        java.util.Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            return booking.getUser() != null && 
                   booking.getUser().getEmail().equals(currentUsername);
        }
        return false;
    }
}