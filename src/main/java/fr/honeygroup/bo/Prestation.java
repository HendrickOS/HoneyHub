package fr.honeygroup.bo;

import java.time.LocalDateTime;

import enumeration.StatutPrestation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PRESTATION")
@Inheritance(strategy = InheritanceType.JOINED) // Garde l'héritage !
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prestation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prestation")
    private Long id; // Integer pour coller à ton SQL INT

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pole", nullable = false)
    private Pole pole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_photo")
    private Photo photo;

    @NotBlank
    @Size(min = 3, max = 255)
    @Column(name = "titre_service", nullable = false)
    private String titreService;

    @NotBlank
    @Size(min = 10, max = 2000)
    @Column(nullable = false, length = 2000)
    private String description;
    
    @NotNull
    @Column(name = "prix_base", nullable = false)
    private Double prixBase; // Ou BigDecimal selon ta préférence

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPrestation statut = StatutPrestation.ACTIF;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }
}