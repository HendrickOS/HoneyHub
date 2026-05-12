package fr.honeygroup.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.honeygroup.bll.PoleService;
import fr.honeygroup.bo.request.PoleRequest;
import fr.honeygroup.bo.response.PoleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/poles")
@RequiredArgsConstructor
@CrossOrigin
public class PoleController {

    private final PoleService poleService;

    // ======================
    // CREATE
    // ======================
    @PostMapping
    public PoleResponse create(@Valid @RequestBody PoleRequest request) {
        return poleService.create(request);
    }

    // ======================
    // GET ALL
    // ======================
    @GetMapping
    public List<PoleResponse> getAll() {
        return poleService.getAll();
    }

    // ======================
    // GET BY ID
    // ======================
    @GetMapping("/{id}")
    public PoleResponse getById(@PathVariable("id") Long id) {
        return poleService.getById(id);
    }

    // ======================
    // GET BY NOM
    // ======================
    @GetMapping("/search")
    public PoleResponse getByNom(@RequestParam String nom) {
        return poleService.getByNom(nom);
    }

    // ======================
    // DELETE
    // ======================
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        poleService.deleteById(id);
    }
}