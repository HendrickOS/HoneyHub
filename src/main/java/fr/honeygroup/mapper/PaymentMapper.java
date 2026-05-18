package fr.honeygroup.mapper;

import fr.honeygroup.bo.Payment;
import fr.honeygroup.bo.response.PaymentResponse;
import org.mapstruct.Mapper;

/**
 * Composant de mapping MapStruct gérant la conversion descendante des données de transactions financières.
 * <p>
 * Enregistré comme Bean Spring via {@code componentModel = "spring"}, ce mapper convertit l'entité de 
 * persistance {@link Payment} en sa représentation sécurisée d'exposition {@link PaymentResponse}. 
 * Il intervient notamment pour alimenter de manière imbriquée l'historique comptable d'un dossier de réservation.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper {

    /**
     * Transforme l'entité comptable {@link Payment} en un DTO de réponse épuré {@link PaymentResponse}.
     * <p>
     * Cette méthode extrait de manière sécurisée les détails de la transaction (méthode, montant, 
     * statut de validation et URL du justificatif) afin de les restituer aux interfaces clientes et de gestion.
     * </p>
     * * @param payment L'entité financière source issue de la base de données MariaDB.
     * @return Le DTO de réponse correspondant, optimisé pour l'affichage et la sérialisation JSON.
     */
    PaymentResponse toResponse(Payment payment);
}