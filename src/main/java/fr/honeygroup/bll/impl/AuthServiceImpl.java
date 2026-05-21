package fr.honeygroup.bll.impl;

import fr.honeygroup.bll.AuthService;
import fr.honeygroup.bo.PasswordResetToken;
import fr.honeygroup.bo.Profile;
import fr.honeygroup.bo.RefreshToken;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.request.*;
import fr.honeygroup.bo.response.TokenResponse;
import fr.honeygroup.enumeration.Role;
import fr.honeygroup.repository.PasswordResetTokenRepository;
import fr.honeygroup.repository.ProfileRepository;
import fr.honeygroup.repository.RefreshTokenRepository;
import fr.honeygroup.repository.UserRepository;
import fr.honeygroup.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation concrete du service de gestion de la securite et des identites (IAM).
 * <p>
 * Cette classe orchestre la securisation des acces de Honey Group. Elle prend en charge 
 * l'authentification Spring Security, l'encodage des mots de passe, le cycle de rafraichissement 
 * des jetons d'acces (JWT) et la gestion des processus critiques de reinitialisation.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository ;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Refresh token expiry: 7 days
    private final long REFRESH_TOKEN_EXPIRATION = 7L * 24 * 60 * 60 * 1000;

    /**
     * {@inheritDoc}
     * <p>
     * Execute l'inscription au sein d'une transaction unique. Verifie de maniere defensive 
     * l'unicite des points d'entree (email et telephone) pour eviter les doublons en base, 
     * chiffre le mot de passe, et lie l'User a son Profile via un cascade de persistence.
     * </p>
     * @throws RuntimeException Si l'adresse email ou le numero de telephone saisis sont deja utilises.
     */
    @Override
    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        if (profileRepository.existsByTelephone(request.getTelephone())) {
            throw new RuntimeException("Telephone already exists");
        }

        User user = User.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.CLIENT)
                .build();

        Profile profile = Profile.builder()
                .user(user)
                .telephone(request.getTelephone())
                .adresse(request.getAdresse())
                .pays(request.getPays())
                .preferences(request.getPreferences())
                .build();
        user.setProfile(profile);

        return userRepository.save(user);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegue la validation des identifiants a l'AuthenticationManager de Spring Security. 
     * Une fois le jeton d'authentification valide, charge l'utilisateur et genere 
     * le couple de tokens (Access / Refresh).
     * </p>
     * @throws RuntimeException Si l'utilisateur s'avere introuvable apres validation.
     */
    @Override
    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return generateTokens(user);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Utilise une programmation fonctionnelle (Streams Optionnels) pour extraire, verifier 
     * l'expiration, charger le compte cible et emettre une nouvelle paire de tokens valides.
     * </p>
     * @throws RuntimeException Si le jeton de rafraichissement est absent ou expire.
     */
    @Override
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        return refreshTokenRepository.findByToken(request.getRefreshToken())
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(this::generateTokens)
                .orElseThrow(() -> new RuntimeException("Refresh token is invalid or expired"));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Invalide la session active en purgeant de maniere transactionnelle le jeton de 
     * rafraichissement associe a l'utilisateur connecte au sein du referentiel.
     * </p>
     */
    @Override
    @Transactional
    public void logout(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            refreshTokenRepository.deleteByUser(user);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Initie le workflow de recuperation. Supprime tout token de recuperation anterieur, 
     * genere un identifiant unique temporaire (UUID v4) d'une validite d'une heure, 
     * et simule temporairement l'envoi de la notification.
     * </p>
     */
    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Clear existing tokens
            passwordResetTokenRepository.deleteByUser(user);

            PasswordResetToken token = PasswordResetToken.builder()
                    .user(user)
                    .token(UUID.randomUUID().toString())
                    .expiryDate(LocalDateTime.now().plusHours(1))
                    .build();
            passwordResetTokenRepository.save(token);

            // Mocking email sending for now
            System.out.println("=================================================");
            System.out.println("PASSWORD RESET EMAIL SENT TO: " + request.getEmail());
            System.out.println("TOKEN: " + token.getToken());
            System.out.println("=================================================");
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Consomme le jeton temporaire d'oubli. Verifie sa validite temporelle, encode le nouveau 
     * mot de passe saisi et execute une purge totale des tokens de session (Refresh) et de 
     * recuperation associes pour des raisons strictes de securite post-modification.
     * </p>
     * @throws RuntimeException Si le jeton est introuvable ou si la date limite de validite est depassee.
     */
    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Invalidate token after use
        passwordResetTokenRepository.delete(resetToken);
        // Also invalidate any existing refresh tokens
        refreshTokenRepository.deleteByUser(user);
    }

    /**
     * Routine interne de generation conjointe des jetons d'identification.
     * @param user L'entite utilisateur cible.
     * @return Le DTO de reponse unifie TokenResponse.
     */
    private TokenResponse generateTokens(User user) {
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = createRefreshToken(user).getToken();
        return TokenResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Routine interne d'instanciation de jeton de rafraichissement.
     * <p>
     * Assure le nettoyage prealable de l'ancien jeton (pattern single-session) avant d'attribuer 
     * un UUID cryptographique unique persiste sous forme de timestamp Instant.
     * </p>
     * @param user L'entite utilisateur proprietaire.
     * @return L'entite RefreshToken persistee.
     */
    private RefreshToken createRefreshToken(User user) {
        // Delete old refresh token if exists
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Valide la conformite temporelle du Refresh Token extrait.
     * <p>
     * Si le jeton a depasse sa date limite, il est automatiquement purge de la base de donnees 
     * pour eviter l'encombrement de tables orphelines.
     * </p>
     * @param token Le jeton a inspecter.
     * @return Le jeton valide s'il n'a pas expire.
     * @throws RuntimeException Si le jeton est obsolete, invitant a une reconnexion complete.
     */
    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }
}