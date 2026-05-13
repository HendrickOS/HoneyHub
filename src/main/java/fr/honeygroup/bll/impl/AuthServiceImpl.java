package fr.honeygroup.bll.impl;

import enumeration.Role;
import fr.honeygroup.bll.AuthService;
import fr.honeygroup.bo.PasswordResetToken;
import fr.honeygroup.bo.Profile;
import fr.honeygroup.bo.RefreshToken;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.request.*;
import fr.honeygroup.bo.response.TokenResponse;
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

      return   userRepository.save(user);

        
    }

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

    @Override
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        return refreshTokenRepository.findByToken(request.getRefreshToken())
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(this::generateTokens)
                .orElseThrow(() -> new RuntimeException("Refresh token is invalid or expired"));
    }

    @Override
    @Transactional
    public void logout(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            refreshTokenRepository.deleteByUser(user);
        }
    }

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

    private TokenResponse generateTokens(User user) {
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = createRefreshToken(user).getToken();
        return TokenResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

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

    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }
}
