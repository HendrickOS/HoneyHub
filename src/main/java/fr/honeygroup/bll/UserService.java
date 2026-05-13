package fr.honeygroup.bll;

import fr.honeygroup.bo.request.ProfileUpdateRequest;
import fr.honeygroup.bo.response.UserProfileResponse;

public interface UserService {
    UserProfileResponse getCurrentUserProfile(String email);
    UserProfileResponse updateProfile(String email, ProfileUpdateRequest request);
}
