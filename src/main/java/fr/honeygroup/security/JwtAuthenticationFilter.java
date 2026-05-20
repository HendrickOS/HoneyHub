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
 * Filtre d'authentification par jeton JWT.
 * <p>
 * Ce composant intercepte chaque requete entrante pour verifier la presence et la validite 
 * d'un jeton JWT dans l'en-tete Authorization. Si le jeton est valide, le contexte de 
 * securite de Spring est peuple avec les informations de l'utilisateur.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Methode principale executant la logique de filtrage par requete.
     * <p>
     * Le filtre :
     * 1. Verifie l'entete "Authorization".
     * 2. Extrait l'email utilisateur du token via {@link JwtService}.
     * 3. Valide le token par rapport aux donnees de l'utilisateur.
     * 4. Enregistre l'authentification dans le {@link SecurityContextHolder}.
     * </p>
     * @param request La requete HTTP entrante.
     * @param response La reponse HTTP sortante.
     * @param filterChain La chaine de filtres a poursuivre.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
     
        final String authHeader = request.getHeader("Authorization");
     
        // =========================
        // 🔥 SAFE CHECK (IMPORTANT)
        // =========================
        if (authHeader == null ||
            authHeader.isBlank() ||
            !authHeader.startsWith("Bearer ")) {
     
            filterChain.doFilter(request, response);
            return;
        }
     
        String jwt = authHeader.substring(7);
     
        if (jwt == null || jwt.equals("null")) {
            filterChain.doFilter(request, response);
            return;
        }
     
        try {
            String userEmail = jwtService.extractUsername(jwt);
     
            if (userEmail != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {
     
                UserDetails userDetails =
                        this.userDetailsService.loadUserByUsername(userEmail);
     
                if (jwtService.isTokenValid(jwt, userDetails)) {
     
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
     
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
     
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
     
        } catch (Exception e) {
            // 🔥 IMPORTANT: NE JAMAIS CRASHER
            filterChain.doFilter(request, response);
            return;
        }
     
        filterChain.doFilter(request, response);
    }
}