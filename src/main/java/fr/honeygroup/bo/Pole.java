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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "POLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pole")
    private Long id;

    @NotBlank(message = "Le nom du pôle est obligatoire")
    @Size(min = 2, max = 100)
    @Column(name = "nom_pole", nullable = false, length = 100)
    private String nom;

    @Lob
    @Column(name = "description")
    private String description;

    @Builder.Default
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    // Relation vers les prestations (One-to-Many)
    // mappedBy fait référence au nom de l'attribut "pole" dans la classe Prestation
    @OneToMany(mappedBy = "pole", cascade = CascadeType.ALL)
    private List<Prestation> prestations;
}