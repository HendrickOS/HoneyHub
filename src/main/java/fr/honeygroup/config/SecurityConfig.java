package fr.honeygroup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import enumeration.Role; // Mes enum

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .csrf(csrf -> csrf.disable()) // Nécessaire pour autoriser les POST depuis Postman
	        .authorizeHttpRequests(auth -> auth
	            // 1. Dashboard Staff : Uniquement ADMIN et MANAGER
	            .requestMatchers("/api/bookings/admin/**").hasAnyRole(Role.ADMIN.name(), Role.MANAGER.name())
	            
	            // 2. Historique Personnel : Accessible par n'importe quel utilisateur connecté (Client, Manager ou Admin)
	            // On utilise .authenticated() car getUtilisateurHistoriquePersonnel() filtrera les données par rapport au login
	            .requestMatchers("/api/bookings/my-bookings").authenticated()
	            
	            // 3. Demande d'annulation (Client) : Tout utilisateur connecté peut demander l'annulation de SA résa
	            .requestMatchers("/api/bookings/cancel-request/**").authenticated()
	            
	            // 4. Réservation : Il faut être connecté pour réserver
	            // Accessible par CLIENT (pour lui-même) et STAFF (pour un client)
	            .requestMatchers("/api/bookings/reserve").authenticated()

	            // 5. Par sécurité, tout le reste demande une authentification
	            .anyRequest().authenticated()
	        )
	        .httpBasic(Customizer.withDefaults()) // Active l'authentification Basic (Username/Password dans Postman)
	    
	        // --- GESTION PERSONNALISÉE DU 403 (FORBIDDEN) ---
	        .exceptionHandling(handling -> handling
	            .accessDeniedHandler((request, response, accessDeniedException) -> {
	                response.setStatus(403);
	                response.setContentType("application/json;charset=UTF-8");
	                
	                // Construction du JSON identique à ton GlobalExceptionHandler
	                String jsonResponse = String.format(
	                    "{\"timestamp\": \"%s\", \"status\": 403, \"error\": \"Forbidden\", \"message\": \"Accès refusé : vous n'avez pas les droits nécessaires pour accéder à cette fonctionnalité.\", \"path\": \"%s\"}",
	                    java.time.LocalDateTime.now(),
	                    request.getRequestURI()
	                );
	                
	                response.getWriter().write(jsonResponse);
	            })
	        );

	    return http.build();
	}

	@Bean
	public InMemoryUserDetailsManager userDetailsService() {
		// {noop} indique à Spring que le mot de passe est en clair (pour le dev)
		UserDetails manager = User	.withUsername("manager@honeygroup.fr")
									.password("{noop}password")
									.roles(Role.MANAGER.name())
									.build();

		UserDetails client = User	.withUsername("client@honeygroup.fr")
									.password("{noop}password")
									.roles(Role.CLIENT.name())
									.build();

		UserDetails admin = User.withUsername("admin@honeygroup.fr")
								.password("{noop}password")
								.roles(Role.ADMIN.name())
								.build();

		return new InMemoryUserDetailsManager(manager, client, admin);
	}
}