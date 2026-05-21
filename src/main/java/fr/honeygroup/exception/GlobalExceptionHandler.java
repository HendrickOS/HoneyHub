package fr.honeygroup.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Intercepteur global des anomalies de l'application (AOP).
 * <p>
 * Centralise la capture des exceptions levées par les différentes couches (BLL, Repositories),
 * les reformate dans une structure JSON standardisée et applique les statuts HTTP 
 * sémantiquement appropriés pour le Frontend.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Méthode utilitaire interne pour construire une réponse d'erreur unifiée.
     * * @param status  Le statut HTTP à retourner.
     * @param error   La catégorie de l'erreur.
     * @param message Le message détaillé expliquant l'anomalie.
     * @return Une instance de {@link ResponseEntity} contenant le corps d'erreur formaté.
     */
    private ResponseEntity<Object> buildResponse(HttpStatus status, String error, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }

    /**
     * Capture les échecs d'authentification (Spring Security).
     * * @param ex L'exception de mauvaises informations d'identification.
     * @return Une réponse 401 UNAUTHORIZED.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", "Email ou mot de passe incorrect");
    }

    /**
     * Gère les incohérences métier détectées dans la BLL.
     * Distinction sémantique : 404 pour les ressources manquantes, 400 pour les règles métier non respectées.
     * * @param ex L'exception métier levée par le service.
     * @return Une réponse 404 ou 400 selon la nature du message d'erreur.
     */
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<Object> handleBusinessLogicException(BusinessLogicException ex) {
        String message = ex.getMessage();
        HttpStatus status = message.toLowerCase().contains("introuvable") 
                            ? HttpStatus.NOT_FOUND 
                            : HttpStatus.BAD_REQUEST;
        return buildResponse(status, "Business Logic Error", message);
    }

    /**
     * Capture les erreurs liées aux capacités logistiques d'une session.
     * * @param ex L'exception de capacité atteinte.
     * @return Une réponse 400 BAD_REQUEST.
     */
    @ExceptionHandler(SessionCapacityException.class)
    public ResponseEntity<Object> handleSessionCapacityException(SessionCapacityException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Session Restriction Error", ex.getMessage());
    }

    /**
     * Capture les violations de sécurité métier (ex: accès à une ressource non autorisée).
     * * @param ex L'exception de sécurité métier.
     * @return Une réponse 403 FORBIDDEN.
     */
    @ExceptionHandler(BusinessSecurityException.class)
    public ResponseEntity<Object> handleBusinessSecurityException(BusinessSecurityException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage());
    }

    /**
     * Intercepte les refus d'accès émis par Spring Security.
     * * @param ex L'exception d'accès refusé.
     * @return Une réponse 403 FORBIDDEN.
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(Exception ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Forbidden", "Vous n'avez pas les droits nécessaires pour cette action.");
    }

    /**
     * Formate les erreurs de validation (Jakarta Validation sur les DTOs).
     * * @param ex L'exception de validation contenant le résultat des erreurs.
     * @return Une réponse 400 BAD_REQUEST avec le premier message d'erreur trouvé.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Erreur de validation des données");
        return buildResponse(HttpStatus.BAD_REQUEST, "Erreur de validation", message);
    }

    /**
     * Gestion de secours pour toute exception non interceptée.
     * * @param ex L'exception système non prévue.
     * @return Une réponse 500 INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Une erreur inattendue est survenue.");
    }

    // ============================================================================
    // CLASSES D'EXCEPTIONS MÉTIERS
    // ============================================================================

    /** Exception levée lorsque les capacités matérielles d'une session sont dépassées. */
    public static class SessionCapacityException extends RuntimeException {
        public SessionCapacityException(String message) { super(message); }
    }

    /** Exception levée lors d'un manquement ou d'une tentative de contournement de la sécurité métier. */
    public static class BusinessSecurityException extends RuntimeException {
        public BusinessSecurityException(String message) { super(message); }
    }

    /** Exception levée lors d'un conflit de validation au niveau des services métiers. */
    public static class BusinessLogicException extends RuntimeException {
        public BusinessLogicException(String message) { super(message); }
    }
}