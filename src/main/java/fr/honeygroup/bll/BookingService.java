package fr.honeygroup.bll;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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
        // 1. Transformation initiale (crée l'objet Booking avec les IDs du DTO)
        Booking booking = bookingMapper.toEntity(request);

        // 2. Chargement des entités complètes (Grâce à tes repositories JpaRepository)
        // On récupère les objets réels pour avoir accès aux champs 'nom', 'titre', etc.
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur ID " + request.getUserId() + " introuvable en base."));
        
        Prestation prestation = prestationRepository.findById(request.getPrestationId())
                .orElseThrow(() -> new RuntimeException("Prestation ID " + request.getPrestationId() + " introuvable."));
        
        Pole pole = poleRepository.findById(request.getPoleId())
                .orElseThrow(() -> new RuntimeException("Pôle ID " + request.getPoleId() + " introuvable."));

        // 3. Injection des objets complets dans le Booking avant la sauvegarde
        booking.setUser(user);
        booking.setPrestation(prestation);
        booking.setPole(pole);

        // 4. Calcul du montant total (Conversion Double -> BigDecimal)
        BigDecimal prixUnitaire = BigDecimal.valueOf(prestation.getPrixBase());
        BigDecimal total = prixUnitaire.multiply(new BigDecimal(request.getNbPersonnes()));
        booking.setMontantTotal(total);

        // 5. Validation automatique pour le mode Sandbox
        booking.setStatut(StatutBooking.CONFIRME);

        // 6. Sauvegarde finale dans MySQL (XAMPP)
        Booking savedBooking = bookingRepository.save(booking);

        // 7. Mapping vers Response : les objets étant "attachés", MapStruct trouvera les noms
        return bookingMapper.toResponse(savedBooking);
    }
    
    /**
     * Nouvelle méthode pour l'historique
     * On utilise readOnly = true pour optimiser la performance des requêtes SELECT
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getHistoriqueUtilisateur(Long userId) {
        return bookingRepository.findByUserIdOrderByDateResaDesc(userId)
                .stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }
}