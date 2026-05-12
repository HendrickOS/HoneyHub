package fr.honeygroup.bll;



import java.util.List;

import fr.honeygroup.bo.request.PoleRequest;
import fr.honeygroup.bo.response.PoleResponse;

public interface PoleService {

    PoleResponse create(PoleRequest request);

    List<PoleResponse> getAll();

    PoleResponse getById(Long id);

    PoleResponse update(Long id, PoleRequest request);

    void delete(Long id);

    PoleResponse getByNom(String nom);

    void deleteById(Long id);
}