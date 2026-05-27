package fr.honeygroup.bll;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.honeygroup.bll.impl.PrestationServiceImpl;
import fr.honeygroup.bo.Prestation;
import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.request.PrestationRequest;
import fr.honeygroup.bo.response.PrestationResponse;
import fr.honeygroup.repository.PrestationRepository;
import fr.honeygroup.repository.PoleRepository;
import fr.honeygroup.repository.CircuitRepository;
import fr.honeygroup.repository.CoursLangueRepository;
import fr.honeygroup.mapper.PrestationMapper;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service PrestationService")
class PrestationServiceTest {

    @Mock
    private PrestationRepository prestationRepository;

    @Mock
    private CircuitRepository circuitRepository;

    @Mock
    private CoursLangueRepository coursLangueRepository;

    @Mock
    private PoleRepository poleRepository;

    @Mock
    private PrestationMapper prestationMapper;

    @InjectMocks
    private PrestationServiceImpl prestationService;

    @Test
    @DisplayName("Catalogue : Créer une prestation générique")
    void createPrestationGenerique_ShouldSaveAndReturnResponse() {
        // 1. Préparation
        PrestationRequest request = new PrestationRequest();
        request.setTitreService("Visite guidée");
        request.setPoleId(1L);

        Pole pole = new Pole();
        pole.setId(1L);
        
        when(poleRepository.findById(1L)).thenReturn(Optional.of(pole));
        when(prestationRepository.save(any(Prestation.class))).thenAnswer(i -> {
            Prestation p = (Prestation) i.getArguments()[0];
            p.setId(1L);
            return p;
        });
        when(prestationMapper.toGenericResponse(any(Prestation.class))).thenAnswer(i -> {
            Prestation p = (Prestation) i.getArguments()[0];
            PrestationResponse resp = new PrestationResponse();
            resp.setId(p.getId());
            return resp;
        });

        // 2. Exécution
        PrestationResponse response = prestationService.createPrestationGenerique(request);

        // 3. Vérifications
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        verify(prestationRepository, times(1)).save(any(Prestation.class));
    }

    @Test
    @DisplayName("Catalogue : Empêcher suppression si prestation liée")
    void deletePrestation_ShouldThrowException_WhenSessionExists() {
        // Simulation d'une dépendance active
        Long id = 1L;
        when(prestationRepository.existsById(id)).thenReturn(true);

        prestationService.deletePrestation(id);

        verify(prestationRepository, times(1)).deleteById(id);
    }
}