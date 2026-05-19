package fr.honeygroup.controller;

import fr.honeygroup.bll.PrestationService;
import fr.honeygroup.bo.request.CircuitRequest;
import fr.honeygroup.bo.request.CoursLangueRequest;
import fr.honeygroup.bo.request.PrestationRequest;
import fr.honeygroup.bo.response.PrestationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestations")
@RequiredArgsConstructor
@CrossOrigin
public class PrestationController {

    private final PrestationService prestationService;

    @GetMapping
    public ResponseEntity<List<PrestationResponse>> getAllPrestations() {
        return ResponseEntity.ok(prestationService.getAllPrestations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestationResponse> getPrestationById(@PathVariable Long id) {
        return ResponseEntity.ok(prestationService.getPrestationById(id));
    }

    @PostMapping("/generique")
    public ResponseEntity<PrestationResponse> createPrestationGenerique(@Valid @RequestBody PrestationRequest request) {
        return ResponseEntity.ok(prestationService.createPrestationGenerique(request));
    }

    @PostMapping("/circuit")
    public ResponseEntity<PrestationResponse> createCircuit(@Valid @RequestBody CircuitRequest request) {
        return ResponseEntity.ok(prestationService.createCircuit(request));
    }

    @PostMapping("/courslangue")
    public ResponseEntity<PrestationResponse> createCoursLangue(@Valid @RequestBody CoursLangueRequest request) {
        return ResponseEntity.ok(prestationService.createCoursLangue(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrestation(@PathVariable Long id) {
        prestationService.deletePrestation(id);
        return ResponseEntity.ok().build();
    }
}
