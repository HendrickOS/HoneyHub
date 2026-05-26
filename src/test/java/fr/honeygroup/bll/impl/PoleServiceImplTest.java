package fr.honeygroup.bll.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.response.PoleResponse;
import fr.honeygroup.mapper.PoleMapper;
import fr.honeygroup.repository.PoleRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests des règles métier de PoleService (BLL)")
class PoleServiceImplTest {

    @Mock
    private PoleRepository poleRepository;

    @Mock
    private PoleMapper poleMapper;

    @Mock
    private MessageSource messageSource; // Injecté pour respecter le constructeur de ton implémentation

    @InjectMocks
    private PoleServiceImpl poleService;

    private Pole poleEcotourisme;
    private PoleResponse poleResponseEcotourisme;

    @BeforeEach
    void setUp() {
        poleEcotourisme = Pole.builder()
                .id(1L)
                .nom("Écotourisme")
                .description("Pôle dédié aux voyages éco-responsables.")
                .build();

        poleResponseEcotourisme = PoleResponse.builder()
                .id(1L)
                .nom("Écotourisme")
                .description("Pôle dédié aux voyages éco-responsables.")
                .build();
    }

    // ============================================================================
    // WORKFLOW DE LECTURE GLOBALE (getAll)
    // ============================================================================

    @Test
    @DisplayName("Extraction globale : Récupération réussie de la liste complète des pôles")
    void getAll_ShouldReturnListOfPoleResponses() {
        // Arrange
        Pole poleIt = Pole.builder().id(2L).nom("IT Outsourcing").build();
        when(poleRepository.findAll()).thenReturn(List.of(poleEcotourisme, poleIt));
        when(poleMapper.toResponse(any(Pole.class))).thenReturn(new PoleResponse());

        // Act
        List<PoleResponse> results = poleService.getAll();

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(poleRepository, times(1)).findAll();
        verify(poleMapper, times(2)).toResponse(any(Pole.class));
    }

    // ============================================================================
    // WORKFLOWS DE RECHERCHE UNITAIRE (getById / getByNom)
    // ============================================================================

    @Test
    @DisplayName("Recherche par ID : Récupération réussie si l'identifiant technique existe")
    void getById_ShouldReturnResponse_WhenIdExists() {
        // Arrange
        when(poleRepository.findById(1L)).thenReturn(Optional.of(poleEcotourisme));
        when(poleMapper.toResponse(poleEcotourisme)).thenReturn(poleResponseEcotourisme);

        // Act
        PoleResponse response = poleService.getById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Écotourisme", response.getNom());
        verify(poleRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Recherche par ID : Échec et levée de RuntimeException si l'identifiant n'existe pas")
    void getById_ShouldThrowRuntimeException_WhenIdNotFound() {
        // Arrange
        when(poleRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> poleService.getById(99L));
        assertEquals("Pôle introuvable", exception.getMessage());
        verify(poleMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Recherche par Nom : Récupération réussie si le libellé exact existe")
    void getByNom_ShouldReturnResponse_WhenNomExists() {
        // Arrange
        when(poleRepository.findByNom("Écotourisme")).thenReturn(Optional.of(poleEcotourisme));
        when(poleMapper.toResponse(poleEcotourisme)).thenReturn(poleResponseEcotourisme);

        // Act
        PoleResponse response = poleService.getByNom("Écotourisme");

        // Assert
        assertNotNull(response);
        assertEquals("Écotourisme", response.getNom());
        verify(poleRepository, times(1)).findByNom("Écotourisme");
    }

    @Test
    @DisplayName("Recherche par Nom : Échec et levée de ResponseStatusException (404) si le libellé n'existe pas")
    void getByNom_ShouldThrowResponseStatusException_WhenNomNotFound() {
        // Arrange
        String nomInexistant = "Pôle Inconnu";
        when(poleRepository.findByNom(nomInexistant)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            poleService.getByNom(nomInexistant)
        );
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Pôle introuvable avec le nom : " + nomInexistant));
        verify(poleMapper, never()).toResponse(any());
    }

    // ============================================================================
    // WORKFLOW DE SUPPRESSION (deleteById)
    // ============================================================================

    @Test
    @DisplayName("Suppression : Purge définitive de la ressource si l'ID existe")
    void deleteById_ShouldDelete_WhenIdExists() {
        // Arrange
        when(poleRepository.findById(1L)).thenReturn(Optional.of(poleEcotourisme));

        // Act & Assert
        assertDoesNotThrow(() -> poleService.deleteById(1L));
        verify(poleRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Suppression : Échec et levée de RuntimeException si le pôle cible n'existe pas")
    void deleteById_ShouldThrowRuntimeException_WhenIdNotFound() {
        // Arrange
        when(poleRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> poleService.deleteById(99L));
        assertEquals("Pôle introuvable", exception.getMessage());
        verify(poleRepository, never()).deleteById(99L);
    }
}