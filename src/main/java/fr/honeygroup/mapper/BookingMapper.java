package fr.honeygroup.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import fr.honeygroup.bo.Booking;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.request.BookingRequest;
import fr.honeygroup.bo.response.BookingResponse;

/**
 * Composant de mapping MapStruct gérant les transformations bidirectionnelles 
 * complexes pour les dossiers de réservation (Booking).
 * <p>
 * En s'appuyant sur {@link PaymentMapper} pour la conversion des flux financiers imbriqués, 
 * ce mapper prend en charge l'aplatissement des relations à travers l'entité pivot Session 
 * pour les sorties API, ainsi que l'isolement des champs d'écriture pour la persistance.
 * </p>
 */
@Mapper(componentModel = "spring", uses = {PaymentMapper.class})
public interface BookingMapper {
    
    // ============================================================================
    // MAPPAGE : VERS LE FRONTEND (Génération de la Réponse API)
    // ============================================================================
    /**
     * Projette et aplatit le graphe d'entités d'une réservation {@link Booking} vers son DTO d'exposition {@link BookingResponse}.
     * <p>
     * Cette méthode extrait de manière ascendante l'arborescence des données (Session, Prestation, Pole) 
     * et applique un formatage civil sur le compte utilisateur via la méthode nommée {@code mapNomComplet}.
     * </p>
     * * @param booking L'entité de persistance source extraite de la base de données.
     * @return Le DTO de réponse configuré pour l'intégration et l'affichage front-end.
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user", target = "userNomComplet", qualifiedByName = "mapNomComplet")
    @Mapping(source = "dateCreationResa", target = "dateResa")
    @Mapping(source = "nbPlaces", target = "nbPersonnes")
    
    // Extraction des données de Pôle et Prestation transitant par la Session (Modèle Normalisé)
    @Mapping(source = "session.id", target = "sessionId")
    @Mapping(source = "session.prestation.pole.id", target = "poleId")
    @Mapping(source = "session.prestation.pole.nom", target = "poleNom")
    @Mapping(source = "session.prestation.id", target = "prestationId")
    @Mapping(source = "session.prestation.titreService", target = "prestationTitre")
    
    // Les informations temporelles de la session pour l'affichage client
    @Mapping(source = "session.dateDebut", target = "dateDebutSession") 
    @Mapping(source = "session.dateFin", target = "dateFinSession")
    
    @Mapping(source = "payments", target = "payments")
    BookingResponse toResponse(Booking booking);
    
    // ============================================================================
    // MAPPAGE : VERS LA BASE DE DONNÉES (Création de l'entité depuis la requête)
    // ============================================================================
    /**
     * Convertit un DTO d'entrée {@link BookingRequest} en entité de persistance {@link Booking}.
     * <p>
     * Les attributs critiques (identifiants techniques, collections de paiements, montants calculés 
     * et statuts de workflow) sont explicitement ignorés ici pour être pris en charge et sécurisés 
     * de manière étanche par la couche métier (BLL).
     * </p>
     * * @param request Le DTO de requête validé émis par le client.
     * @return L'entité de base initialisée, prête pour les traitements métier du service.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "montantTotal", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(source = "userId", target = "user.id")
    
    // Liaison pivot de la requête vers la structure Session
    @Mapping(source = "sessionId", target = "session.id")
    @Mapping(source = "nbPersonnes", target = "nbPlaces")
    Booking toEntity(BookingRequest request);

    /**
     * Méthode de conversion personnalisée (Custom Mapping Helper) nommée.
     * <p>
     * Agrège et civilise les propriétés d'identité d'un utilisateur en forçant le nom de famille 
     * en lettres majuscules professionnelles. Retourne une valeur par défaut sécurisée si l'objet est nul.
     * </p>
     * * @param user L'entité utilisateur associée au dossier.
     * @return Une chaîne de caractères nettoyée et formatée sous la forme "NOM Prénom".
     */
    @Named("mapNomComplet")
    default String mapNomComplet(User user) {
        if (user == null) return "Client inconnu";
        String nom = (user.getNom() != null) ? user.getNom().toUpperCase() : "";
        String prenom = (user.getPrenom() != null) ? user.getPrenom() : "";
        return (nom + " " + prenom).trim();
    }
}