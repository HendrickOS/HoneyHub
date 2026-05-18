package fr.honeygroup.bo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entité représentant une prestation de formation linguistique (CoursLangue).
 * <p>
 * Cette classe spécialise l'entité générique {@link Prestation} en exploitant les mécanismes 
 * d'héritage d'Hibernate (généralement configurés via une stratégie de type JOINED ou SINGLE_TABLE 
 * sur la classe parente). Elle enrichit le catalogue avec des caractéristiques pédagogiques propres 
 * aux modules d'apprentissage de langues.
 * </p>
 */
@Entity
@Table(name = "COURS_LANGUE") // Alignement strict sur la casse et le nommage du script SQL MariaDB
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true) // Crucial : Intègre les propriétés de la classe parente Prestation dans les calculs d'égalité
public class CoursLangue extends Prestation {

    /**
     * La langue enseignée au cours de la formation (ex: "Anglais Professionnel", "Malgache Immersif").
     */
    @NotBlank(message = "La langue enseignée est obligatoire")
    @Size(max = 100)
    @Column(name = "langue", nullable = false, length = 100)
    private String langue;

    /**
     * Le niveau de compétence visé ou requis pour s'inscrire au module (ex: "Débutant (A1)", "Intermédiaire (B2)").
     */
    @NotBlank(message = "Le niveau requis ou visé est obligatoire (ex: B1, Débutant...)")
    @Size(max = 50)
    @Column(name = "niveau", nullable = false, length = 50)
    private String niveau;

    /**
     * Contenu pédagogique détaillé et déroulé du programme de formation.
     * L'annotation @Lob mappe ce champ en tant qu'objet textuel lourd (LONGTEXT/TEXT) en base de données.
     */
    @NotBlank(message = "Le descriptif du programme est obligatoire")
    @Size(min = 20, max = 5000)
    @Lob
    @Column(name = "descriptif_programme")
    private String descriptifProgramme;
}