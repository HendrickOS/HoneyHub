package fr.honeygroup.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "DETAILS_SPECIFIQUES") // Majuscules pour le SQL
@Getter @Setter // Préférable à @Data pour les relations ManyToOne
@NoArgsConstructor @AllArgsConstructor
@Builder
@ToString(exclude = "demandeLead")
public class DetailsSpecifiques {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detail")
    private Integer id;

    @NotBlank(message = "La clé du champ est obligatoire")
    @Column(name = "champ_cle", nullable = false, length = 100)
    private String champCle;

    @Lob // Pour le LONGTEXT de ton script
    @Column(name = "valeur")
    private String valeur;

    @Lob // Manquant chez le collègue, présent dans ton SQL
    @Column(name = "valeur_json")
    private String valeurJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demande", nullable = false) // Doit correspondre au script SQL
    @JsonIgnore // Pour éviter les boucles infinies dans tes futurs contrôleurs
    private DemandeLead demandeLead;
}