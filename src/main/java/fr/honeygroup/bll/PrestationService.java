package fr.honeygroup.bll;

import fr.honeygroup.bo.request.CircuitRequest;
import fr.honeygroup.bo.request.CoursLangueRequest;
import fr.honeygroup.bo.request.PrestationRequest;
import fr.honeygroup.bo.response.PrestationResponse;

import java.util.List;

public interface PrestationService {
    List<PrestationResponse> getAllPrestations();
    PrestationResponse getPrestationById(Long id);
    PrestationResponse createPrestationGenerique(PrestationRequest request);
    PrestationResponse createCircuit(CircuitRequest request);
    PrestationResponse createCoursLangue(CoursLangueRequest request);
    void deletePrestation(Long id);
}
