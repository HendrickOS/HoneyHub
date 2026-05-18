package fr.honeygroup.bo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entité représentant une ressource multimédia (Photo) au sein du système.
 * <p>
 * Cette classe gère l'arborescence et le référencement des images stockées (sur serveur local 
 * ou service de stockage Cloud) afin d'illustrer visuellement les prestations du catalogue 
 * (circuits écotouristiques, séjours, etc.).
 * </p>
 */
@Entity
@Table(name = "PHOTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Photo {

    /**
     * Identifiant unique et clé primaire de la ressource photo.
     * Généré de façon automatisée via un mécanisme d'auto-incrément natif dans le SGBD.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_photo")
    private Long id;

    /**
     * Chemin d'accès ou URL complète vers le fichier image hébergé.
     * Contrainte de validation stricte pour garantir l'intégrité du rendu sur les interfaces front-end.
     */
    @NotBlank(message = "L'URL du fichier est obligatoire")
    @Size(max = 500, message = "L'URL est trop longue (max 500 caractères)")
    @Column(name = "url_fichier", nullable = false, length = 500)
    private String urlFichier;

    /**
     * Texte alternatif ou description textuelle courte associée à l'image.
     * Utilisé pour enrichir l'affichage utilisateur ou assurer l'accessibilité numérique (balises alt).
     */
    @Size(max = 255, message = "La légende est trop longue (max 255 caractères)")
    @Column(name = "legende", length = 255)
    private String legende;
}