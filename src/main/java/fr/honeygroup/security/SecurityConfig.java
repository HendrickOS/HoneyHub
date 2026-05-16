package fr.honeygroup.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {}) // Enable default cors config if any
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        
                        // ===== POLES =====
                        // Lecture des pôles ouverte à tous les connectés
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/poles/**").permitAll()
                        // Création, modification, suppression réservées à l'ADMIN
                        .requestMatchers("/api/poles/**").hasRole("ADMIN")
                        
                        // ===== PRESTATIONS (Circuits, Cours, etc.) =====
                        // Lecture ouverte à tous
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/prestations/**", "/api/circuits/**", "/api/courslangues/**").permitAll()
                        // Ajout/Suppression/Modif réservés à l'ADMIN
                        .requestMatchers("/api/prestations/**", "/api/circuits/**", "/api/courslangues/**").hasRole("ADMIN")

                        // ===== DEMANDES DE LEADS =====
                        // Le client peut soumettre (POST) une demande de lead
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/leads/**").hasAnyRole("CLIENT", "ADMIN")
                        // Le manager et l'admin peuvent lire et traiter (PUT) les leads
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/leads/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/leads/**").hasAnyRole("MANAGER", "ADMIN")
                        // Seul l'admin peut supprimer un lead
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/leads/**").hasRole("ADMIN")

                        // ===== UTILISATEURS / PROFILS =====
                        // N'importe quel utilisateur connecté peut gérer son propre profil
                        .requestMatchers("/api/users/me/**").authenticated()
                        // Gestion globale des utilisateurs (voir tout le monde, supprimer) = ADMIN
                        .requestMatchers("/api/users/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
