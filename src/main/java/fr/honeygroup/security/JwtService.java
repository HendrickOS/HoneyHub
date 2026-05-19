package fr.honeygroup.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Service utilitaire pour la gestion des jetons JSON Web Token (JWT).
 * <p>
 * Ce service centralise toutes les operations liees a la securite stateless :
 * generation de jetons, extraction des donnees contenues dans les jetons (claims),
 * verification de leur validite et gestion de la signature cryptographique HMAC-SHA256.
 * </p>
 */
@Service
public class JwtService {

    /** Cle secrete utilisee pour signer les jetons (chargee depuis la configuration). */
    @Value("${application.security.jwt.secret-key:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;
    
    /** Duree de validite des jetons en millisecondes. */
    @Value("${application.security.jwt.expiration:86400000}")
    private long jwtExpiration;

    /**
     * Extrait le nom d'utilisateur (subject) depuis le jeton.
     * @param token Le jeton JWT.
     * @return Le nom d'utilisateur (email).
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait une information specifique (claim) depuis le jeton.
     * @param token Le jeton JWT.
     * @param claimsResolver Fonction pour resoudre le claim souhaite.
     * @return La valeur du claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Genere un jeton pour un utilisateur sans claims supplementaires.
     * @param userDetails Les details de l'utilisateur.
     * @return Le jeton genere.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Genere un jeton avec des claims supplementaires.
     * @param extraClaims Map des claims supplementaires.
     * @param userDetails Les details de l'utilisateur.
     * @return Le jeton genere.
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Construit le jeton JWT.
     */
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Verifie si le jeton est valide pour l'utilisateur donne.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Verifie si la date d'expiration du jeton est depassee.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Parse et extrait tous les claims du jeton.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Genere la cle de signature a partir de la secretKey decodee en Base64.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}