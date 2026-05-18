package fr.honeygroup.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import enumeration.Role;
import fr.honeygroup.repository.UserRepository;

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
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) 
            .csrf(csrf -> csrf.disable()) 
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
            .httpBasic(Customizer.withDefaults()) 
            
            // --- GESTION PERSONNALISÉE DU REJET D'HABILITATION 403 (FORBIDDEN) ---
            .exceptionHandling(handling -> handling
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json;charset=UTF-8");
                    
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
     * Configure le fournisseur de données d'authentification de l'application.
     * <p>
     * Cette méthode connecte les filtres de Spring Security à la base de données MySQL via le dépôt
     * {@link UserRepository}, permettant une vérification dynamique des identifiants (e-mail et mot de passe chiffré BCrypt).
     * </p>
     *
     * @param userRepository Le dépôt d'accès aux données de l'entité User.
     * @return Une implémentation fonctionnelle de {@link UserDetailsService} connectée à la base de données.
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByEmail(username)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword()) 
                        // Utiliser .roles() applique automatiquement le préfixe ROLE_ requis par .hasAnyRole()
                        .roles(user.getRole().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'adresse : " + username));
    }

    /**
     * Déclare le moteur de chiffrement et de vérification des mots de passe pour l'application.
     * <p>
     * L'implémentation choisie est {@link BCryptPasswordEncoder}, un algorithme de hachage robuste 
     * intégrant un sel aléatoire, aligné sur les empreintes stockées dans le script d'initialisation de la base.
     * </p>
     *
     * @return L'instance du composant d'encodage BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Permet de lire à la fois les mots de passe en {noop}text et d'autres formats standards
        return org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * Source de configuration globale des politiques de partage de ressources Cross-Origin (CORS).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); 
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}