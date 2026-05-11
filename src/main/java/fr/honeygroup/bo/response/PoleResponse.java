package fr.honeygroup.bo.response; // Ou fr.honeygroup.bo.response selon ton choix de package

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PoleResponse {

    private Integer id; // On garde Integer pour coller à ton SQL INT
    private String nom;
    private String description;
}