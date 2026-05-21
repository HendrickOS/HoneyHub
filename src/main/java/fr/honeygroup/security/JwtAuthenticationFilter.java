package fr.honeygroup.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre de sécurité d'interception et d'authentification par jeton JWT.
 * <p>
 * Ce composant intercepte chaque requête HTTP entrante (une seule fois par cycle d'appel) 
 * afin d'inspecter et de valider la présence d'un jeton porteur (Bearer Token) dans l'en-tête Authorization.
 * Si le jeton est formellement valide, le contexte de sécurité de Spring Security est peuplé de manière 
 * stateless, conférant à l'utilisateur les autorités (rôles) nécessaires pour consommer les API sécurisées.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Intercepte la requête, extrait le jeton JWT, vérifie son intégrité et configure 
     * le contexte d'authentification de l'application de manière sécurisée.
     * <p>
     * Le cycle de contrôle applique les étapes de traitement suivantes :
     * <ol>
     * <li>Validation de l'en-tête "Authorization" (vérification de la présence du préfixe "Bearer ").</li>
     * <li>Isolation et extraction de la chaîne brute du jeton avec protection contre les payloads invalides (chaînes "null").</li>
     * <li>Extraction de l'identifiant (email) et vérification de l'absence d'une authentification préexistante.</li>
     * <li>Chargement des détails de l'utilisateur et validation de la signature/expiration du token via {@link JwtService}.</li>
     * <li>Injecte l'objet {@link UsernamePasswordAuthenticationToken} validé dans le {@link SecurityContextHolder}.</li>
     * </ol>
     * En cas de jeton invalide, expiré ou corrompu, le filtre intercepte silencieusement l'exception afin 
     * de ne jamais interrompre la chaîne de traitement (Failsafe Pattern), déléguant le rejet d'accès aux guards d'URL.
     * </p>
     *
     * @param request La requête HTTP entrante.
     * @param response La réponse HTTP sortante.
     * @param filterChain La chaîne de filtres de sécurité à poursuivre.
     * @throws ServletException En cas d'anomalie d'interception liée au conteneur de servlets.
     * @throws IOException En cas de rupture des flux d'E/S réseau.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
         
        final String authHeader = request.getHeader("Authorization");
         
        // --- ÉTAPE 1 : Validation de premier niveau de l'en-tête HTTP ---
        if (authHeader == null ||
            authHeader.isBlank() ||
            !authHeader.startsWith("Bearer ")) {
         
            filterChain.doFilter(request, response);
            return;
        }
         
        // Extraction de la chaîne de caractères après "Bearer " (7 caractères)
        String jwt = authHeader.substring(7);
         
        // --- ÉTAPE 2 : Protection défensive contre les transmissions de tokens vides ou textuellement "null" ---
        if (jwt == null || jwt.equals("null")) {
            filterChain.doFilter(request, response);
            return;
        }
         
        try {
            // --- ÉTAPE 3 : Extraction de l'identité de l'utilisateur (username/email) ---
            String userEmail = jwtService.extractUsername(jwt);
         
            // Si l'émetteur est identifié et que la requête n'est pas déjà authentifiée dans le thread courant
            if (userEmail != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {
         
                // Récupération du profil utilisateur depuis le fournisseur de persistance
                UserDetails userDetails =
                        this.userDetailsService.loadUserByUsername(userEmail);
         
                // --- ÉTAPE 4 : Validation de la signature cryptographique et des revendications du jeton ---
                if (jwtService.isTokenValid(jwt, userDetails)) {
         
                    // Construction du jeton d'authentification Spring Security
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
         
                    // Encapitulation des détails de la requête d'origine (IP, Session)
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
         
                    // --- ÉTAPE 5 : Hydratation du contexte de sécurité Spring ---
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
         
        } catch (Exception e) {
            // --- PROTECTION SÉCURITÉ : Pattern Failsafe ---
            // En cas de levée d'une exception (ExpiredJwtException, MalformedJwtException, etc.),
            // l'erreur est absorbée. On refuse l'authentification tout en permettant à la chaîne de continuer 
            // pour laisser les points de terminaison publics (comme la création de lead visiteur) accessibles.
            filterChain.doFilter(request, response);
            return;
        }
         
        // Poursuite standard du cycle de filtrage
        filterChain.doFilter(request, response);
    }
}