package fr.honeygroup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import enumeration.Role;
import java.util.List;

/**
 * Classe de configuration principale de la sécurité applicative basée sur Spring Security 6+.
 * <p>
 * Ce composant configure la chaîne de filtres de sécurité (SecurityFilterChain) afin de durcir l'accès 
 * à l'API REST de Honey Group. Il applique le contrôle d'accès basé sur les rôles (RBAC), désactive la protection CSRF 
 * pour l'architecture Stateless de démonstration, intègre la politique CORS et standardise le formatage 
 * des rejets d'authentification (403 Forbidden).
 * </p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Définit la politique d'accès et d'interception des requêtes HTTP (SecurityFilterChain).
     * <p>
     * <strong>Mécanisme de sécurité :</strong> Les requêtes sont évaluées de manière séquentielle, du cas le plus 
     * spécifique au plus générique. Les endpoints sensibles de gestion (Backoffice) sont limités au personnel habilité, 
     * les parcours transactionnels clients exigent une authentification, tandis que le catalogue d'offres reste public.
     * </p>
     * * @param http Objet constructeur de la configuration de sécurité web.
     * @return La chaîne de filtres configurée et instanciée.
     * @throws Exception En cas d'erreur de configuration interne des modules Spring Security.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Injection de la politique CORS globale
            .csrf(csrf -> csrf.disable()) // Désactivation du jeton CSRF pour fluidifier les tests d'API (Postman)
            .authorizeHttpRequests(auth -> auth
                
                // 1. Accès Public : Consultation du catalogue (Pôles/Prestations) et soumission du tunnel de prospection (Leads)
                .requestMatchers(HttpMethod.GET, "/api/poles/**", "/api/prestations/**", "/api/sessions/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/leads").permitAll()
                
                // 2. Dashboard Staff : Isolation des actions de gestion (Admin & Manager)
                .requestMatchers("/api/bookings/admin/**").hasAnyRole(Role.ADMIN.name(), Role.MANAGER.name())
                
                // 3. Espace Personnel Client : Historique et demandes d'annulation contractuelles
                .requestMatchers("/api/bookings/my-bookings").authenticated()
                .requestMatchers("/api/bookings/cancel-request/**").authenticated()
                
                // 4. Actes d'achat / Réservations : Exigence d'un contexte de sécurité authentifié
                .requestMatchers("/api/bookings/reserve").authenticated()

                // 5. Règle parapluie défensive : Tout autre point d'accès non explicite exige une authentification brute
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults()) // Activation du schéma HTTP Basic Authentication pour le développement
            
            // --- GESTION PERSONNALISÉE DU REJET D'HABILITATION 403 (FORBIDDEN) ---
            .exceptionHandling(handling -> handling
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json;charset=UTF-8");
                    
                    // Unification de la structure de réponse JSON avec celle du GlobalExceptionHandler
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

    /**
     * Structure un fournisseur d'utilisateurs éphémère en mémoire (Sandboxing de développement).
     * <p>
     * <strong>Note d'architecture :</strong> Ce gestionnaire provisionne un jeu de comptes de test typés 
     * pour l'évaluation des rôles de l'application. Le préfixe {@code {noop}} configure le délégué pour ignorer 
     * le chiffrement de surface (mots de passe stockés en clair), configuration strictement réservée à l'environnement de recette.
     * </p>
     * * @return Une instance d'{@link InMemoryUserDetailsManager} pré-alimentée.
     */
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails manager = User.withUsername("manager@honeygroup.fr")
                                    .password("{noop}password")
                                    .roles(Role.MANAGER.name())
                                    .build();

        UserDetails client = User.withUsername("client@honeygroup.fr")
                                    .password("{noop}password")
                                    .roles(Role.CLIENT.name())
                                    .build();

        UserDetails admin = User.withUsername("admin@honeygroup.fr")
                                .password("{noop}password")
                                .roles(Role.ADMIN.name())
                                .build();

        return new InMemoryUserDetailsManager(manager, client, admin);
    }

    /**
     * Source de configuration globale des politiques de partage de ressources Cross-Origin (CORS).
     * <p>
     * Assure l'ouverture contrôlée de l'API pour autoriser les requêtes asynchrones (AJAX/Fetch) 
     * provenant d'architectures découplées (Frontend de type SPA), tout en spécifiant les verbes 
     * HTTP et en-têtes autorisés.
     * </p>
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // En production, restreindre au nom de domaine du front (ex: "http://localhost:3000")
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}