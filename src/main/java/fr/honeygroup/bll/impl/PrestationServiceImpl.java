package fr.honeygroup.bll.impl;

import enumeration.StatutPrestation;
import fr.honeygroup.bll.PrestationService;
import fr.honeygroup.bo.Circuit;
import fr.honeygroup.bo.CoursLangue;
import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.Prestation;
import fr.honeygroup.bo.request.CircuitRequest;
import fr.honeygroup.bo.request.CoursLangueRequest;
import fr.honeygroup.bo.request.PrestationRequest;
import fr.honeygroup.bo.response.PrestationResponse;
import fr.honeygroup.mapper.PrestationMapper;
import fr.honeygroup.repository.CircuitRepository;
import fr.honeygroup.repository.CoursLangueRepository;
import fr.honeygroup.repository.PoleRepository;
import fr.honeygroup.repository.PrestationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrestationServiceImpl implements PrestationService {

    private final PrestationRepository prestationRepository;
    private final CircuitRepository circuitRepository;
    private final CoursLangueRepository coursLangueRepository;
    private final PoleRepository poleRepository;
    private final PrestationMapper prestationMapper;

    @Override
    public List<PrestationResponse> getAllPrestations() {
        return prestationRepository.findAll().stream()
                .map(prestationMapper::toGenericResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PrestationResponse getPrestationById(Long id) {
        Prestation prestation = prestationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prestation introuvable"));
        return prestationMapper.toGenericResponse(prestation);
    }

    @Override
    @Transactional
    public PrestationResponse createPrestationGenerique(PrestationRequest request) {
        Pole pole = getPole(request.getPoleId());
        
        Prestation prestation = new Prestation();
        mapCommonFields(prestation, request, pole);
        
        return prestationMapper.toGenericResponse(prestationRepository.save(prestation));
    }

    @Override
    @Transactional
    public PrestationResponse createCircuit(CircuitRequest request) {
        Pole pole = getPole(request.getPoleId());
        
        Circuit circuit = new Circuit();
        mapCommonFields(circuit, request, pole);
        circuit.setDescriptionLongue(request.getDescriptionLongue());
        circuit.setItineraire(request.getItineraire());
        circuit.setDuree(request.getDuree());
        
        return prestationMapper.toGenericResponse(circuitRepository.save(circuit));
    }

    @Override
    @Transactional
    public PrestationResponse createCoursLangue(CoursLangueRequest request) {
        Pole pole = getPole(request.getPoleId());
        
        CoursLangue coursLangue = new CoursLangue();
        mapCommonFields(coursLangue, request, pole);
        coursLangue.setLangue(request.getLangue());
        coursLangue.setNiveau(request.getNiveau());
        coursLangue.setDescriptifProgramme(request.getDescriptifProgramme());
        
        return prestationMapper.toGenericResponse(coursLangueRepository.save(coursLangue));
    }

    @Override
    @Transactional
    public void deletePrestation(Long id) {
        if (!prestationRepository.existsById(id)) {
            throw new RuntimeException("Prestation introuvable");
        }
        prestationRepository.deleteById(id);
    }

    private Pole getPole(Long poleId) {
        return poleRepository.findById(poleId)
                .orElseThrow(() -> new RuntimeException("Pole introuvable"));
    }

    private void mapCommonFields(Prestation prestation, PrestationRequest request, Pole pole) {
        prestation.setPole(pole);
        prestation.setTitreService(request.getTitreService());
        prestation.setDescription(request.getDescription());
        prestation.setPrixBase(request.getPrixBase());
        prestation.setStatut(request.getStatut() != null ? request.getStatut() : StatutPrestation.ACTIF);
    }
}
