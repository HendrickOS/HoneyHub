package fr.honeygroup.mapper;

import fr.honeygroup.bo.Booking;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.response.BookingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {PaymentMapper.class})
public interface BookingMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user", target = "userNomComplet", qualifiedByName = "mapNomComplet")
    @Mapping(source = "pole.id", target = "poleId")
    @Mapping(source = "pole.nom", target = "poleNom")
    @Mapping(source = "prestation.id", target = "prestationId")
    @Mapping(source = "prestation.titreService", target = "prestationTitre")
    @Mapping(source = "payments", target = "payments") // Se fera via PaymentMapper
    BookingResponse toResponse(Booking booking);

    // Méthode pour transformer l'objet User en une String "NOM Prénom"
    @Named("mapNomComplet")
    default String mapNomComplet(User user) {
        if (user == null) return "Client inconnu";
        String nom = (user.getNom() != null) ? user.getNom().toUpperCase() : "";
        String prenom = (user.getPrenom() != null) ? user.getPrenom() : "";
        return (nom + " " + prenom).trim();
    }
}