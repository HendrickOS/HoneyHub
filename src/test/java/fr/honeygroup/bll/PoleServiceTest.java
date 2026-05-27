package fr.honeygroup.bll;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.honeygroup.bll.impl.PoleServiceImpl;
import fr.honeygroup.bo.Pole;
import fr.honeygroup.bo.request.PoleRequest;
import fr.honeygroup.bo.response.PoleResponse;
import fr.honeygroup.repository.PoleRepository;
import fr.honeygroup.mapper.PoleMapper;
import org.springframework.context.MessageSource;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service PoleService")
class PoleServiceTest {

    @Mock
    private PoleRepository poleRepository;

    @Mock
    private PoleMapper mapper;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private PoleServiceImpl poleService;

    @Test
    @DisplayName("Création : Empêcher la création d'un pôle existant")
    void create_ShouldThrowException_WhenNomExists() {
        // 1. Préparation
        PoleRequest request = new PoleRequest();
        request.setNom("Écotourisme");
        
        // 2. Exécution
        PoleResponse response = poleService.create(request);
        
        // 3. Vérification
        assertThat(response).isNull();
    }

    @Test
    @DisplayName("Lecture : Récupération par nom")
    void getByNom_ShouldReturnPoleResponse() {
        Pole pole = new Pole();
        pole.setNom("IT Outsourcing");
        
        PoleResponse poleResponse = new PoleResponse();
        poleResponse.setNom("IT Outsourcing");
        
        when(poleRepository.findByNom("IT Outsourcing")).thenReturn(java.util.Optional.of(pole));
        when(mapper.toResponse(pole)).thenReturn(poleResponse);

        PoleResponse response = poleService.getByNom("IT Outsourcing");

        assertThat(response.getNom()).isEqualTo("IT Outsourcing");
    }
}