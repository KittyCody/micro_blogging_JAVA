package io.micro_blogger.server.controller;

import io.micro_blogger.server.common.ApiError;
import io.micro_blogger.server.common.Result;
import io.micro_blogger.server.dto.UpdateUserProfileRequest;
import io.micro_blogger.server.viewmodel.UserProfileViewModel;
import io.micro_blogger.server.service.userProfile.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

//    /**
//     * @param username
//     * @return
//     */

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileViewModel> getUserProfile(@PathVariable String username) {
        Result<UserProfileViewModel> userProfileResult = userProfileService.getUserProfile(username);

        if (userProfileResult.isSuccess()) {
            return ResponseEntity.ok(userProfileResult.getValue());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorViewModel(userProfileResult.getError()));
        }
    }

    @PostMapping("/{username}/avatar")
    public ResponseEntity<UserProfileViewModel> uploadAvatar(
            @PathVariable String username,
            @RequestParam("avatar") MultipartFile avatarFile,
            Principal principal) {

        String userId = principal.getName();
        Result<UserProfileViewModel> result = userProfileService.updateAvatar(username, avatarFile, userId);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getValue());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorViewModel(result.getError()));
        }
    }

    @PatchMapping("/{username}/userDetails")
    public ResponseEntity<UserProfileViewModel> updateUserProfile(
            @PathVariable String username,
            @RequestBody UpdateUserProfileRequest request,
            Principal principal) {

        String userId = principal.getName();
        Result<UserProfileViewModel> result = userProfileService.updateUserProfile(username, request, userId);

        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getValue());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorViewModel(result.getError()));
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUserProfile(@PathVariable String username, Principal principal) {
        String userId = principal.getName();
        Result<Void> result = userProfileService.deleteUserProfile(username, userId);

        if (result.isSuccess()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserProfileViewModel>> searchUserProfiles(
            @RequestParam String username, Principal principal) {

        String currentUser = principal.getName();
        List<UserProfileViewModel> foundProfiles = userProfileService.searchUserProfiles(username, currentUser);
        return ResponseEntity.ok(foundProfiles);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserProfileViewModel>> getAllUserProfiles() {
        List<UserProfileViewModel> userProfileResponses = userProfileService.getAllUserProfiles();
        return ResponseEntity.ok(userProfileResponses);
    }

    private UserProfileViewModel createErrorViewModel(ApiError error) {
        return new UserProfileViewModel(
                null,
                null,
                null,
                "Error",
                0,
                0,
                LocalDateTime.now()
        );
    }
}
