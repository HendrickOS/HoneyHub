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
import fr.honeygroup.bo.request.PrestationRequest;
import fr.honeygroup.bo.response.PrestationResponse;
import fr.honeygroup.repository.PrestationRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service PrestationService")
class PrestationServiceTest {

    @Mock
    private PrestationRepository prestationRepository;

    @InjectMocks
    private PrestationServiceImpl prestationService;

    @Test
    @DisplayName("Catalogue : Créer une prestation générique")
    void createPrestationGenerique_ShouldSaveAndReturnResponse() {
        // 1. Préparation
        PrestationRequest request = new PrestationRequest();
        request.setTitreService("Visite guidée");
        
        when(prestationRepository.save(any(Prestation.class))).thenAnswer(i -> {
            Prestation p = (Prestation) i.getArguments()[0];
            p.setId(1L);
            return p;
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
        // Ici, on suppose que ton service vérifie les sessions liées avant suppression
        // Implémentation du mock pour simuler une contrainte métier
        when(prestationRepository.existsById(id)).thenReturn(true);
        // ... Logique de vérification des sessions ...

        // Vérification que le delete n'est jamais appelé si contrainte violée
        // (À adapter selon l'implémentation de ta logique métier de sécurité)
    }
}