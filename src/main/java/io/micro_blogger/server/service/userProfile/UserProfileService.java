package io.micro_blogger.server.service.userProfile;

import io.micro_blogger.server.dto.UpdateUserProfileRequest;
import io.micro_blogger.server.model.Account;
import io.micro_blogger.server.model.UserProfile;
import io.micro_blogger.server.viewmodel.UserProfileViewModel;
import io.micro_blogger.server.common.Result;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface UserProfileService {
    Result<UserProfileViewModel> getUserProfile(String username);

    Result<UserProfileViewModel> updateUserProfile(String username, UpdateUserProfileRequest request, String userId);

    Result<Void> deleteUserProfile(String username, String userId);

    void createForAccount(Account account);

    List<UserProfileViewModel> searchUserProfiles(String username, String currentUser);

    Result<UserProfileViewModel> updateAvatar(String username, MultipartFile avatarFile, String userId);

    UserProfileViewModel toResponse(UserProfile userProfile);

    List<UserProfileViewModel> getAllUserProfiles();
}
