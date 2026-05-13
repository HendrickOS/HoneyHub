package fr.honeygroup.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import fr.honeygroup.bo.Booking;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.request.BookingRequest;
import fr.honeygroup.bo.response.BookingResponse;

@Mapper(componentModel = "spring", uses = {PaymentMapper.class})
public interface BookingMapper {
	
	// --- Vers le Front (Response) ---
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user", target = "userNomComplet", qualifiedByName = "mapNomComplet")
    @Mapping(source = "pole.id", target = "poleId")
    @Mapping(source = "pole.nom", target = "poleNom")
    @Mapping(source = "prestation.id", target = "prestationId")
    @Mapping(source = "prestation.titreService", target = "prestationTitre")
    @Mapping(source = "payments", target = "payments") // Se fera via PaymentMapper
    BookingResponse toResponse(Booking booking);
    
    // --- Vers la Base de données (Request) ---
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "dateSouhaitee", target = "dateResa")
    @Mapping(target = "statut", constant = "EN_ATTENTE")
    @Mapping(target = "payments", ignore = true)
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "prestationId", target = "prestation.id")
    @Mapping(source = "poleId", target = "pole.id")
    @Mapping(target = "montantTotal", ignore = true) // À calculer dans le Service
    Booking toEntity(BookingRequest request);

    // Méthode pour transformer l'objet User en une String "NOM Prénom"
    @Named("mapNomComplet")
    default String mapNomComplet(User user) {
        if (user == null) return "Client inconnu";
        String nom = (user.getNom() != null) ? user.getNom().toUpperCase() : "";
        String prenom = (user.getPrenom() != null) ? user.getPrenom() : "";
        return (nom + " " + prenom).trim();
    }
}