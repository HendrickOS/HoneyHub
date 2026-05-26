package fr.honeygroup.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.honeygroup.bll.PaymentService;
import fr.honeygroup.bo.request.PaymentRequest;
import fr.honeygroup.bo.response.PaymentResponse;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser // Simule un utilisateur authentifié basique
    void getPayment_ShouldReturn200() throws Exception {
        Long paymentId = 1L;
        PaymentResponse mockResponse = new PaymentResponse(); // Assure-toi que @NoArgsConstructor est présent sur PaymentResponse
        
        when(paymentService.getPaymentDetails(paymentId)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/payments/{id}", paymentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    void getMyPayments_ShouldReturn200() throws Exception {
        when(paymentService.getPaymentsForCurrentUser()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/payments/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    void confirmerPaiement_ShouldReturn200AndMessage() throws Exception {
        Long paymentId = 1L;
        PaymentRequest request = new PaymentRequest();
        // Optionnel : request.setTransactionId("TX-123"); request.setMethode("VIREMENT");

        // Comme on a passé le retour du service à void, Mockito le gère implicitement, pas besoin de 'when'

        mockMvc.perform(post("/api/payments/{paymentId}/confirmer", paymentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Votre demande a bien été envoyée, un staff se chargera de la validation."));
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Simule spécifiquement un rôle ADMIN pour passer le @PreAuthorize
    void validerPaiement_ShouldReturn200AndUpdatedStatus() throws Exception {
        Long paymentId = 1L;

        // when(paymentService.validerPaiement(paymentId)).thenReturn("Paiement validé avec succès..."); 
        // Si ton service renvoie un String, décommente la ligne ci-dessus. Sinon, laisse le mock agir en void.

        mockMvc.perform(post("/api/payments/{id}/valider", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VALIDE"))
                .andExpect(jsonPath("$.bookingStatus").value("CONFIRME"));
    }

    @Test
    @WithMockUser(roles = "MANAGER") // Le rôle MANAGER doit aussi pouvoir accéder
    void rejeterPaiement_ShouldReturn200AndRejectedStatus() throws Exception {
        Long paymentId = 1L;
        
        when(paymentService.rejeterPaiement(paymentId)).thenReturn("Le paiement a été rejeté.");

        mockMvc.perform(post("/api/payments/{id}/rejeter", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJETE"))
                .andExpect(jsonPath("$.message").value("Le paiement a été rejeté."));
    }
}