package fr.honeygroup.bll.impl;

import fr.honeygroup.bll.UserService;
import fr.honeygroup.bo.Profile;
import fr.honeygroup.bo.User;
import fr.honeygroup.bo.request.ProfileUpdateRequest;
import fr.honeygroup.bo.response.UserProfileResponse;
import fr.honeygroup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserProfileResponse getCurrentUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(String email, ProfileUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getNom() != null && !request.getNom().isEmpty()) {
            user.setNom(request.getNom());
        }
        if (request.getPrenom() != null && !request.getPrenom().isEmpty()) {
            user.setPrenom(request.getPrenom());
        }

        Profile profile = user.getProfile();
        if (profile == null) {
            profile = new Profile();
            profile.setUser(user);
            user.setProfile(profile);
        }

        if (request.getAdresse() != null) profile.setAdresse(request.getAdresse());
        if (request.getTelephone() != null) profile.setTelephone(request.getTelephone());
        if (request.getPays() != null) profile.setPays(request.getPays());
        if (request.getPreferences() != null) profile.setPreferences(request.getPreferences());

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    private UserProfileResponse mapToResponse(User user) {
        Profile profile = user.getProfile();
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .role(user.getRole().name())
                .adresse(profile != null ? profile.getAdresse() : null)
                .telephone(profile != null ? profile.getTelephone() : null)
                .pays(profile != null ? profile.getPays() : null)
                .preferences(profile != null ? profile.getPreferences() : null)
                .build();
    }
}
