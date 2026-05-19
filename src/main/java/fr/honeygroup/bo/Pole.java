package fr.honeygroup.bo;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entité représentant un pôle d'activité principal (Pole) au sein de Honey Group.
 * <p>
 * Cette table structure l'organisation de l'entreprise en séparant de manière 
 * macroscopique le pôle Écotourisme (voyages, circuits) du pôle IT Outsourcing 
 * (prestations et développements sur-mesure).
 * </p>
 */
@Entity
@Table(name = "POLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pole {

    /**
     * Identifiant unique et clé primaire du pôle d'activité.
     * Généré automatiquement par incrémentation séquentielle côté base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pole")
    private Long id;

    /**
     * Libellé unique désignant le pôle d'activité (ex: "Écotourisme", "IT Outsourcing").
     * Contrainte de validation stricte empêchant les chaînes vides ou nulles.
     */
    @NotBlank(message = "Le nom du pôle est obligatoire")
    @Size(min = 2, max = 100)
    @Column(name = "nom_pole", nullable = false, length = 100)
    private String nom;

    /**
     * Description détaillée des services, de la vision et de la charte du pôle d'activité.
     * L'annotation @Lob mappe ce champ en tant qu'objet textuel lourd (TEXT/LONGTEXT) en base MariaDB.
     */
    @Lob
    @Column(name = "description")
    private String description;

    /**
     * Date et heure de l'enregistrement initial du pôle dans le système.
     * Champ verrouillé contre toute tentative de mise à jour ultérieure afin de sécuriser l'historique.
     */
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    /**
     * Relation bidirectionnelle vers le catalogue des prestations rattachées à ce pôle.
     * <p>
     * La cascade complète permet de propager l'ensemble des opérations d'écriture de manière cohérente.
     * L'annotation @ToString.Exclude prévient les chargements cycliques intempestifs lors de la journalisation.
     * </p>
     */
    @OneToMany(mappedBy = "pole", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Prestation> prestations;

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }
}