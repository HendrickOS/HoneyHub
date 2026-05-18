package fr.honeygroup.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Intercepteur global des anomalies de l'application (AOP - Aspect Oriented Programming).
 * <p>
 * Centralise la capture des exceptions levées par les couches inférieures (BLL, Repositories) 
 * afin de les reformater dans une structure JSON standardisée et sémantiquement correcte 
 * (statuts HTTP adaptés) pour le Frontend.
 * </p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Capture les erreurs d'intégrité ou de capacité liées à la logistique des sessions temporelles
     * (ex: jauge maximale d'inscrits atteinte, session expirée).
     * * @param ex L'exception de capacité ou de validité de session capturée.
     * @return Une réponse HTTP 400 BAD_REQUEST contenant le détail textuel du blocage métier.
     */
    @ExceptionHandler(SessionCapacityException.class)
    public ResponseEntity<Object> handleSessionCapacityException(SessionCapacityException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Session Restriction Error");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Capture les violations de sécurité contextuelles détectées au cœur de la logique métier 
     * (ex: tentative de fraude IDOR, substitution d'identité non autorisée pour un tiers).
     * * @param ex L'exception de sécurité applicative interceptée.
     * @return Une réponse HTTP 403 FORBIDDEN scellée.
     */
    @ExceptionHandler(BusinessSecurityException.class)
    public ResponseEntity<Object> handleBusinessSecurityException(BusinessSecurityException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "Forbidden");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    /**
     * Gestion globale des erreurs d'exécution internes imprévues (RuntimeException non spécialisées).
     * * @param ex L'anomalie système ou l'erreur de logique brute interceptée.
     * @return Une réponse HTTP 505 INTERNAL_SERVER_ERROR masquant les détails sensibles de l'infrastructure.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Business Logic Error");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Intercepte les refus d'accès d'infrastructure émis nativement par Spring Security 
     * (ex: un client tentant de solliciter un endpoint annoté @PreAuthorize("hasRole('ADMIN')")).
     * * @param ex L'anomalie de droits Spring Security.
     * @return Une réponse HTTP 403 FORBIDDEN formalisée.
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "Forbidden");
        body.put("message", "Vous n'avez pas les droits nécessaires pour cette action.");

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }
    
    /**
     * Formate et isole les erreurs de validation de surface levées par Jakarta Validation 
     * sur les DTOs d'entrée annotés {@code @Valid} au niveau des contrôleurs.
     * * @param ex L'anomalie contenant l'arbre des champs non conformes.
     * @return Une réponse HTTP 400 BAD_REQUEST portant le libellé d'erreur (éventuellement internationalisé).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Erreur de validation");
        
        String message = "Erreur de validation";
        FieldError fieldError = ex.getBindingResult().getFieldError();
        if (fieldError != null) {
            message = fieldError.getDefaultMessage();
        }
        
        body.put("message", message);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // ============================================================================
    // CLASSES EXTENSIONS D'EXCEPTIONS MÉTIERS (À isoler ou maintenir ici)
    // ============================================================================

    /**
     * Exception levée lorsque les capacités matérielles d'une session fixe sont dépassées.
     */
    public static class SessionCapacityException extends RuntimeException {
        public SessionCapacityException(String message) {
            super(message);
        }
    }

    /**
     * Exception levée lors d'un manquement ou d'une tentative de contournement de la sécurité métier (IDOR).
     */
    public static class BusinessSecurityException extends RuntimeException {
        public BusinessSecurityException(String message) {
            super(message);
        }
    }
}