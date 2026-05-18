package fr.honeygroup.bo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entité représentant une prestation de voyage organisé sous forme de circuit (Circuit).
 * <p>
 * Cette classe hérite de l'entité générique {@link Prestation}. Elle modélise et structure 
 * le catalogue d'expériences du pôle Écotourisme en y apportant les détails logistiques 
 * indispensables aux séjours immersifs (itinéraires complexes, durées formelles, descriptions exhaustives).
 * </p>
 */
@Entity
@Table(name = "CIRCUIT") // Alignement strict sur la casse et le nommage du script SQL MariaDB
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true) // Crucial : Incorpore les attributs de la classe parente Prestation pour le calcul d'égalité des instances
public class Circuit extends Prestation {

    /**
     * Présentation éditoriale complète et détaillée mettant en valeur le séjour.
     * Mappé au format LONGTEXT/TEXT grâce à @Lob pour stocker un contenu textuel ou enrichi conséquent.
     */
    @NotBlank(message = "La description longue est obligatoire")
    @Size(min = 20, max = 5000)
    @Lob
    @Column(name = "description_longue")
    private String descriptionLongue;

    /**
     * Déroulé chronologique pas-à-pas et étapes clés du voyage (ex: escales, hébergements, activités incluses).
     * Mappé avec l'annotation @Lob pour offrir une flexibilité maximale sur la mise en page ou l'insertion de listes d'étapes.
     */
    @NotBlank(message = "L'itinéraire est obligatoire")
    @Lob
    @Column(name = "itineraire", nullable = false)
    private String itineraire;

    /**
     * Indicateur de temporalité commerciale du séjour (ex: "7 jours / 6 nuits", "14 jours").
     * Permet au client de visualiser instantanément le format du voyage sur les interfaces front-end.
     */
    @NotBlank(message = "La durée est obligatoire (ex: 7 jours / 6 nuits)")
    @Column(name = "duree", nullable = false, length = 100)
    private String duree;
}