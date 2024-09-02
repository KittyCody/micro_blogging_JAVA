package io.micro_blogger.server.service.userProfile;

import io.micro_blogger.server.common.CommonErrors;
import io.micro_blogger.server.dto.UpdateUserProfileRequest;
import io.micro_blogger.server.model.Account;
import io.micro_blogger.server.model.UserProfile;
import io.micro_blogger.server.service.account.AccountService;
import io.micro_blogger.server.service.security.S3Service;
import io.micro_blogger.server.viewmodel.UserProfileViewModel;
import io.micro_blogger.server.repository.UserProfileRepo;
import io.micro_blogger.server.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private UserProfileRepo userProfileRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private AccountService accountService;

    @Override
    public Result<UserProfileViewModel> getUserProfile(String username) {
        UserProfile userProfile = userProfileRepository.findByUsername(username).orElse(null);
        if (userProfile == null) {
            return Result.failure(CommonErrors.ENTITY_NOT_PRESENT);
        }
        return Result.success(toResponse(userProfile));
    }

    @Transactional
    @Override
    public Result<UserProfileViewModel> updateUserProfile(String username, UpdateUserProfileRequest request, String userId) {
        Optional<Account> authenticatedAccount = accountService.findById(UUID.fromString(userId));
        String authenticatedUsername = authenticatedAccount.map(Account::getUsername).orElse("Not found");

        if (authenticatedAccount.isEmpty() || !authenticatedUsername.equals(username)) {
            return Result.failure(CommonErrors.FORBIDDEN_OPERATION);
        }

        UserProfile userProfile = userProfileRepository.findByUsername(username).orElse(null);
        if (userProfile == null) {
            return Result.failure(CommonErrors.ENTITY_NOT_PRESENT);
        }

        if (request.getBiography() != null) {
            userProfile.setBio(request.getBiography());
        }
        if (request.getEmail() != null) {
            userProfile.setEmail(request.getEmail());
        }
        if (request.getUsername() != null && !request.getUsername().equals(userProfile.getUsername())) {
            if (userProfileRepository.findByUsername(request.getUsername()).isPresent()) {
                return Result.failure(CommonErrors.ACCOUNT_ALREADY_EXISTS);
            }
            userProfile.setUsername(request.getUsername());
        }

        userProfileRepository.save(userProfile);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            UserDetails newUserDetails = userDetailsService.loadUserByUsername(userProfile.getUsername());
            Authentication newAuthentication = new UsernamePasswordAuthenticationToken(newUserDetails, authentication.getCredentials(), newUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        }

        return Result.success(toResponse(userProfile));
    }

    @Override
    public Result<Void> deleteUserProfile(String username, String userId) {
        Optional<Account> authenticatedAccount = accountService.findById(UUID.fromString(userId));

        UserProfile userProfile = userProfileRepository.findByUsername(username).orElse(null);
        if (userProfile == null) {
            return Result.failure(CommonErrors.ENTITY_NOT_PRESENT);
        }

        if (authenticatedAccount.isEmpty() || !authenticatedAccount.get().getUsername().equals(username)) {
            return Result.failure(CommonErrors.FORBIDDEN_OPERATION);
        }

        userProfileRepository.delete(userProfile);
        return Result.success(null);
    }

    @Override
    public void createForAccount(Account account) {
        UserProfile userProfile = new UserProfile(account);
        userProfile.setUsername(account.getUsername());
        userProfile.setBio("");

        UserProfile savedProfile = this.userProfileRepository.save(userProfile);
        toResponse(savedProfile);
    }

    @Override
    public List<UserProfileViewModel> searchUserProfiles(String username, String currentUser) {
        List<UserProfile> userProfiles = userProfileRepository.findByUsernameFuzzy(username);
        return userProfiles.stream()
                .filter(userProfile -> !userProfile.getUsername().equalsIgnoreCase(currentUser))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Result<UserProfileViewModel> updateAvatar(String username, MultipartFile avatarFile, String userId) {
        Optional<Account> authenticatedAccount = accountService.findById(UUID.fromString(userId));
        if (authenticatedAccount.isEmpty() || !authenticatedAccount.get().getUsername().equals(username)) {
            return Result.failure(CommonErrors.FORBIDDEN_OPERATION);
        }

        UserProfile userProfile = userProfileRepository.findByUsername(username).orElse(null);
        if (userProfile == null) {
            return Result.failure(CommonErrors.ENTITY_NOT_PRESENT);
        }

        try {
            String avatarUrl = String.valueOf(s3Service.uploadImage(avatarFile));
            userProfile.setAvatar(avatarUrl);
            userProfileRepository.save(userProfile);
            return Result.success(toResponse(userProfile));
        } catch (IOException e) {
            return Result.failure(CommonErrors.AVATAR_UPLOAD_FAILED);
        }
    }

    @Override
    public UserProfileViewModel toResponse(UserProfile userProfile) {
        return new UserProfileViewModel(
                userProfile.getAvatar(),
                userProfile.getUsername(),
                userProfile.getEmail(),
                userProfile.getBio(),
                userProfile.getFollowerCount(),
                userProfile.getFolloweeCount(),
                userProfile.getRegistrationDate()
        );
    }

    @Override
    public List<UserProfileViewModel> getAllUserProfiles() {
        List<UserProfile> userProfiles = userProfileRepository.findAll();
        return userProfiles.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
