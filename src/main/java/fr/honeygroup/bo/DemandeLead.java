package fr.honeygroup.bo;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import enumeration.StatutLead;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "DEMANDE_LEAD")
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "pole", "prestation", "specificDetails"})
public class DemandeLead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_demande")
    private Long id;

    @Column(name = "date_soumission", nullable = false, updatable = false)
    private LocalDateTime dateSoumission;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_traitement", nullable = false, length = 50)
    private StatutLead statut;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String source ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pole", nullable = false)
    @JsonIgnore
    private Pole pole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prestation")
    @JsonIgnore
    private Prestation prestation;

    @Lob
    @Column(name = "commentaire_interne")
    private String commentaireInterne;

    @OneToMany(mappedBy = "demandeLead", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetailsSpecifiques> specificDetails;

    @PrePersist
    public void prePersist() {
        if (dateSoumission == null) dateSoumission = LocalDateTime.now();
        if (statut == null) statut = StatutLead.NOUVEAU;
    }
}