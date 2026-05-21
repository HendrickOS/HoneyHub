package fr.honeygroup.security;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import fr.honeygroup.enumeration.Role;

/**
 * Configuration maîtresse de la sécurité applicative (Spring Security 6+ & JWT).
 * <p>
 * Ce composant centralise l'infrastructure de protection de l'API Honey Group :
 * <ul>
 * <li>Contrôle d'accès basé sur les rôles (RBAC) pour l'Admin, le Manager et le Client.</li>
 * <li>Architecture de sécurité de type REST Stateless pilotée par Token JWT.</li>
 * <li>Politique de partage de ressources cross-origin (CORS).</li>
 * <li>Formatage sémantique des exceptions de droits (403 Forbidden) renvoyées au Frontend.</li>
 * </ul>
 * </p>
 */
/**
 * Configuration maîtresse de la sécurité applicative (Spring Security 6+ & JWT).
 * Active la sécurisation fine par annotations (@PreAuthorize) au niveau des méthodes.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Active la sécurisation fine par annotations (@PreAuthorize) au niveau de la BLL
@RequiredArgsConstructor
public class SecurityConfig {

    /** Filtre d'interception et de validation des jetons JWT à chaque requête entrante. */
    private final JwtAuthenticationFilter jwtAuthFilter;
    
    /** Service d'extraction et de chargement des identités utilisateurs depuis la base MySQL. */
    private final UserDetailsService userDetailsService;

    /**
     * Spécifie la chaîne de filtres de sécurité (SecurityFilterChain) et configure les règles de routage HTTP.
     * * @param http Le constructeur de configuration de sécurité HTTP de Spring.
     * @return La chaîne de filtrage configurée et instanciée.
     * @throws Exception En cas d'erreur de parsing des règles de sécurité.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Injection de la configuration CORS globale
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) 
            
            // Désactivation du CSRF car l'API est Stateless (les tokens JWT sont insensibles aux failles CSRF classiques)
            .csrf(csrf -> csrf.disable()) 
            
            // Définition de la cartographie des droits d'accès par URL (RBAC)
            .authorizeHttpRequests(auth -> auth
                
                // 1. ACCES PUBLIC CRITIQUE
                // Autorise l'authentification/inscription, la documentation Swagger, et la lecture des catalogues
                .requestMatchers("/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/poles/**", "/api/prestations/**", "/api/circuits/**", "/api/courslangues/**", "/api/sessions/**").permitAll()
                
                // 2. TUNNEL D'ACQUISITION DES OPPORTUNITES (LEADS)
                // Tout prospect non connecté peut soumettre son besoin, mais la gestion est isolée pour le Staff
                .requestMatchers(HttpMethod.POST, "/api/leads/**").permitAll() 
                .requestMatchers(HttpMethod.GET, "/api/leads/**").hasAnyRole(Role.MANAGER.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/api/leads/**").hasAnyRole(Role.MANAGER.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/api/leads/**").hasRole(Role.ADMIN.name())

                // 3. GESTION DES REFERENTIELS (POLES & PRESTATIONS)
                // Seul l'administrateur système est habilité à modifier l'offre commerciale globale
                .requestMatchers("/api/poles/**", "/api/prestations/**", "/api/circuits/**", "/api/courslangues/**").hasRole(Role.ADMIN.name())
                
                // 4. LOGISTIQUE DES RESERVATIONS (BOOKINGS)
                // Cloisonnement entre les vues opérationnelles du Staff et le périmètre d'un client connecté
                .requestMatchers("/api/bookings/admin/**").hasAnyRole(Role.ADMIN.name(), Role.MANAGER.name())
                .requestMatchers("/api/bookings/my-bookings", "/api/bookings/cancel-request/**", "/api/bookings/reserve").authenticated()
                
                // 5. COMPTES ET PROFILS UTILISATEURS
                // Un utilisateur gère ses propres données, l'Admin supervise l'ensemble des comptes
                .requestMatchers("/api/users/me/**").authenticated()
                .requestMatchers("/api/users/**").hasRole(Role.ADMIN.name())

                // 6. REGLE PARAPLUIE PAR DEFAUT
                // Toute route oubliée ou non listée explicitement ci-dessus requiert une authentification obligatoire
                .anyRequest().authenticated()
            )
            
            // Configuration de la stratégie d'absence d'état (Stateless) : aucune session HTTP n'est créée côté serveur
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Liaison du gestionnaire de vérification des identités
            .authenticationProvider(authenticationProvider())
            
            // Greffe du filtre JWT de Fatima juste avant le filtre d'authentification classique par mot de passe
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            
            // --- GESTION PERSONNALISEE DU REJET D'HABILITATION 403 (FORBIDDEN) ---
            // Formate l'erreur brute de Spring en un JSON standardisé et clair pour le développeur Front
            .exceptionHandling(handling -> handling
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json;charset=UTF-8");
                    
                    String jsonResponse = String.format(
                        "{\"timestamp\": \"%s\", \"status\": 403, \"error\": \"Forbidden\", \"message\": \"Accès refusé : vous n'avez pas les droits nécessaires pour cette action.\", \"path\": \"%s\"}",
                        java.time.LocalDateTime.now(),
                        request.getRequestURI()
                    );
                    
                    response.getWriter().write(jsonResponse);
                })
            );

        return http.build();
    }
    
    /**
     * Instancie le fournisseur d'authentification par défaut de l'application.
     * <p>
     * Ce Bean configure un {@link DaoAuthenticationProvider} en lui injectant le service de recherche 
     * des utilisateurs et le moteur d'encodage des empreintes de mots de passe.
     * </p>
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Expose le gestionnaire d'authentification global de Spring Security.
     * <p>
     * Ce composant est indispensable au sein du contrôleur d'authentification pour valider 
     * les requêtes de Login brutes fournies par l'utilisateur.
     * </p>
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Déclare l'encodeur de mots de passe officiel de l'infrastructure.
     * <p>
     * Utilise un mécanisme de délégation flexible permettant de décoder de manière transparente 
     * le BCrypt standard ainsi que le format textuel brut {@code {noop}} utile pour les jeux de données de tests.
     * </p>
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * Établit la politique globale CORS (Cross-Origin Resource Sharing).
     * <p>
     * Indispensable en phase de développement pour autoriser les frameworks clients Frontend (ex: Angular, React) 
     * hébergés sur un autre port ou domaine à consommer les ressources et endpoints de cette API.
     * </p>
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // Permet l'accès depuis n'importe quelle origine source
        // TODO: En production, remplacer "*" par l'URL réelle de ton application Frontend
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}