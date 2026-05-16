package fr.honeygroup.exception;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	 @ExceptionHandler(BadCredentialsException.class)
	    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {

	        Map<String, String> error = new HashMap<>();
	        error.put("message", "Email ou mot de passe incorrect");

	        return ResponseEntity
	                .status(HttpStatus.UNAUTHORIZED)
	                .body(error);
	    }

	@ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        // On renvoie le message de l'exception avec un code 400
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
	
}