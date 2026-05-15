package fr.honeygroup.bll;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import enumeration.StatutBooking;
import fr.honeygroup.bo.Booking;
import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.Prestation;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.request.BookingRequest;
import fr.honeygroup.bo.response.BookingResponse;
import fr.honeygroup.mapper.BookingMapper;
import fr.honeygroup.repository.BookingRepository;
import fr.honeygroup.repository.PoleRepository;
import fr.honeygroup.repository.PrestationRepository;
import fr.honeygroup.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PrestationRepository prestationRepository;
    private final UserRepository userRepository;
    private final PoleRepository poleRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    public BookingResponse creerReservationSandbox(BookingRequest request) {
        // 1. Transformation initiale
        Booking booking = bookingMapper.toEntity(request);

        // 2. Chargement des entités complètes
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur ID " + request.getUserId() + " introuvable."));
        
        Prestation prestation = prestationRepository.findById(request.getPrestationId())
                .orElseThrow(() -> new RuntimeException("Prestation ID " + request.getPrestationId() + " introuvable."));
        
        Pole pole = poleRepository.findById(request.getPoleId())
                .orElseThrow(() -> new RuntimeException("Pôle ID " + request.getPoleId() + " introuvable."));

        // 3. Injection des objets complets
        booking.setUser(user);
        booking.setPrestation(prestation);
        booking.setPole(pole);

        // 4. Calcul du montant total
        BigDecimal prixUnitaire = BigDecimal.valueOf(prestation.getPrixBase());
        BigDecimal total = prixUnitaire.multiply(new BigDecimal(request.getNbPersonnes()));
        booking.setMontantTotal(total);

        // 5. Validation automatique
        booking.setStatut(StatutBooking.CONFIRME);

        // 6. Sauvegarde
        Booking savedBooking = bookingRepository.save(booking);

        // 7. Mapping vers Response
        return bookingMapper.toResponse(savedBooking);
    }
    
    /**
     * VUE CLIENT (Option A) : Récupère l'historique personnel de l'utilisateur connecté.
     * Sécurité : Pas d'ID en paramètre, impossible de tricher sur l'identité.
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getUtilisateurHistoriquePersonnel() {
        // Récupération de l'email (username) de l'utilisateur authentifié dans Postman
        String emailConnecte = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Recherche de l'utilisateur en base pour récupérer son ID réel
        User user = userRepository.findByEmail(emailConnecte)
                .orElseThrow(() -> new RuntimeException("Utilisateur [" + emailConnecte + "] non trouvé en base."));

        return bookingRepository.findByUserIdOrderByDateResaDesc(user.getId())
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }
    
    /**
     * VUE ADMIN/MANAGER : Consulter le dossier complet d'un client spécifique.
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getDossierClientPourStaff(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("L'utilisateur avec l'ID " + userId + " n'existe pas.");
        }

        return bookingRepository.findByUserIdOrderByDateResaDesc(userId)
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }
}