package fr.honeygroup.bo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Objet de transfert de donnees (DTO) specifique destine a la creation ou modification d'un cours de langue.
 * <p>
 * Cette classe etend {@link PrestationRequest} et herite de l'ensemble de ses attributs et de sa validation 
 * de socle commun. Elle s'appuie sur {@link SuperBuilder} pour heriter du pattern de construction et sur 
 * {@link EqualsAndHashCode} pour inclure les proprietes parentes dans les verifications d'egalite.
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CoursLangueRequest extends PrestationRequest {

    /**
     * La langue etudiee ou enseignee lors de la prestation (ex: Anglais, Espagnol, Japonais).
     * <p>
     * Contraintes : Obligatoire, ne peut pas etre vide et limitee a un maximum de 100 caracteres.
     * </p>
     */
    @NotBlank(message = "La langue enseignée est obligatoire")
    @Size(max = 100)
    private String langue;

    /**
     * Le niveau de competence requis, vise ou cible par la formation pedagogique.
     * <p>
     * Contraintes : Obligatoire, limite a 50 caracteres maximum (ex: 'B1', 'Debutant', 'Avance').
     * </p>
     */
    @NotBlank(message = "Le niveau requis ou visé est obligatoire (ex: B1, Débutant...)")
    @Size(max = 50)
    private String niveau;

    /**
     * Bloc textuel exhaustif detaillant le deroule, le contenu et le programme du cours de langue.
     * <p>
     * Contraintes : Obligatoire, taille bornee de maniere stricte entre 20 et 5000 caracteres 
     * pour assurer un descriptif pedagogique complet et structure.
     * </p>
     */
    @NotBlank(message = "Le descriptif du programme est obligatoire")
    @Size(min = 20, max = 5000)
    private String descriptifProgramme;
}