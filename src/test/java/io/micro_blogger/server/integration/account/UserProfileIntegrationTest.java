//package io.micro_blogger.server.integration.account;
//
//import io.micro_blogger.server.common.CommonErrors;
//import io.micro_blogger.server.common.Result;
//import io.micro_blogger.server.model.Account;
//import io.micro_blogger.server.model.UserProfile;
//import io.micro_blogger.server.repository.AccountRepo;
//import io.micro_blogger.server.repository.UserProfileRepo;
//import io.micro_blogger.server.integration.IntegrationTestBase;
//import io.micro_blogger.server.service.userProfile.UserProfileServiceImpl;
//import io.micro_blogger.server.dto.UpdateUserProfileRequest;
//import io.micro_blogger.server.viewmodel.UserProfileViewModel;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.List;
//import java.util.Optional;
//import static org.junit.jupiter.api.Assertions.*;
//
//@Transactional
//public class UserProfileIntegrationTest extends IntegrationTestBase {
//
//    @Autowired
//    private UserProfileRepo userProfileRepo;
//
//    @Autowired
//    private AccountRepo accountRepo;
//
//    @Autowired
//    private UserProfileServiceImpl userProfileService;
//
//    private Account testAccount;
//    private UserProfile testUserProfile;
//    private String userId;
//
//    @BeforeEach
//    public void setUp() {
//        testAccount = new Account();
//        testAccount.setUsername("testUser");
//        testAccount.setPassword("password");
//        testAccount = accountRepo.save(testAccount);
//
//        testUserProfile = new UserProfile(testAccount);
//        testUserProfile.setUsername("testUser");
//        testUserProfile.setEmail("test@example.com");
//        testUserProfile.setBio("Test biography");
//        testUserProfile.setAvatar("http://example.com/avatar.jpg");
//        testUserProfile = userProfileRepo.save(testUserProfile);
//
//        userId = testAccount.getId().toString();
//    }
//
//    @Test
//    public void testCreateUserProfile() {
//        UserProfile newProfile = new UserProfile(testAccount);
//        newProfile.setUsername("newUser");
//        newProfile.setEmail("new@example.com");
//        newProfile.setBio("New biography");
//
//        UserProfile createdProfile = userProfileRepo.save(newProfile);
//        assertNotNull(createdProfile.getId(), "UserProfile ID should not be null");
//        assertEquals(newProfile.getUsername(), createdProfile.getUsername(), "Usernames should match");
//    }
//
//    @Test
//    public void testFindUserProfileById() {
//        Optional<UserProfile> foundProfile = userProfileRepo.findById(testUserProfile.getId());
//        System.out.println("Found Profile: " + foundProfile);
//        assertTrue(foundProfile.isPresent(), "UserProfile should be found");
//        assertEquals(testUserProfile.getUsername(), foundProfile.get().getUsername(), "Usernames should match");
//    }
//
//
//    @Test
//    public void testCreateUserProfileWithNullBiography() {
//        UserProfile userProfile = new UserProfile(testAccount);
//        userProfile.setBio(null);
//        userProfile.setUsername("testUser2");
//        userProfile.setEmail("testUser2@example.com");
//        userProfileRepo.save(userProfile);
//
//        assertNotNull(userProfile.getId(), "UserProfile should be created even with null bio");
//    }
//
//    @Test
//    public void testUpdateUserProfile() {
//        // Given
//        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
//        request.setBiography("Updated biography");
//        request.setUsername("UpdatedUser");
//        request.setEmail("updated@example.com");
//
//        // When
//        Result<UserProfileViewModel> result = userProfileService.updateUserProfile(testUserProfile.getUsername(), request, userId);
//
//        // Then
//        assertTrue(result.isSuccess(), "Expected successful update of user profile");
//
//        UserProfileViewModel updatedProfile = result.getValue();
//
//        // Use the default getters provided by the record
//        assertEquals("Updated biography", updatedProfile.biography(), "UserProfile biography should be updated");
//        assertEquals("UpdatedUser", updatedProfile.username(), "UserProfile username should be updated");
//        assertEquals("updated@example.com", updatedProfile.email(), "UserProfile email should be updated");
//    }
//
//
//    @Test
//    public void testFuzzySearchProfiles() {
//        // Given: Creating additional user profiles for testing fuzzy search
//        Account testAccount1 = new Account();
//        testAccount1.setUsername("testUser1");
//        testAccount1.setPassword("password123");
//        accountRepo.save(testAccount1);
//
//        UserProfile userProfile1 = new UserProfile(testAccount1);
//        userProfile1.setEmail("user1@example.com");
//        userProfile1.setBio("Bio for user 1");
//        userProfile1.setAvatar("http://example.com/avatar1.jpg");
//        userProfile1.setUsername("user1");
//        userProfileRepo.save(userProfile1);
//
//        Account testAccount2 = new Account();
//        testAccount2.setUsername("testUser2");
//        testAccount2.setPassword("password456");
//        accountRepo.save(testAccount2);
//
//        UserProfile userProfile2 = new UserProfile(testAccount2);
//        userProfile2.setEmail("user2@example.com");
//        userProfile2.setBio("Bio for user 2");
//        userProfile2.setAvatar("http://example.com/avatar2.jpg");
//        userProfile2.setUsername("user2");
//        userProfileRepo.save(userProfile2);
//
//        // Assume testAccount is the current user and its profile is excluded from search results
//        Account testAccount = new Account(); // This should be the current user setup, adjust as needed
//        testAccount.setUsername("testUserCurrent");
//        testAccount.setPassword("password789");
//        accountRepo.save(testAccount);
//
//        UserProfile currentUserProfile = new UserProfile(testAccount);
//        currentUserProfile.setEmail("currentuser@example.com");
//        currentUserProfile.setBio("Current user bio");
//        currentUserProfile.setAvatar("http://example.com/avatarcurrent.jpg");
//        currentUserProfile.setUsername("currentuser");
//        userProfileRepo.save(currentUserProfile);
//
//        String searchTerm = "user";
//        List<UserProfileViewModel> foundProfiles = userProfileService.searchUserProfiles(searchTerm, currentUserProfile.getUsername());
//
//        // Then: Validate search results
//        assertFalse(foundProfiles.isEmpty(), "Should find profiles matching the fuzzy search term");
//        assertEquals(3, foundProfiles.size(), "Should find two user profiles matching 'user'");
//        assertFalse(foundProfiles.stream().anyMatch(profile -> profile.username().equals(currentUserProfile.getUsername())), "Current user profile should not be included in the results");
//    }
//
//    @Test
//    public void testGetUserProfileNotFound() {
//        Result<UserProfileViewModel> result = userProfileService.getUserProfile("nonExistentUser");
//        assertTrue(result.isFailure(), "Expected failure when fetching non-existent user profile");
//        assertEquals(CommonErrors.ENTITY_NOT_PRESENT.getCode(), result.getError().getCode());
//    }
//
//    @Test
//    public void testFuzzySearchNoResults() {
//        String currentUsername = testAccount.getUsername();
//        List<UserProfileViewModel> foundProfiles = userProfileService.searchUserProfiles("unknownUser", currentUsername);
//        assertTrue(foundProfiles.isEmpty(), "No profiles should be found");
//    }
//
//    @Test
//    public void testDeleteUserProfileSuccessfully() {
//        Result<Void> result = userProfileService.deleteUserProfile(testUserProfile.getUsername(), userId);
//        assertTrue(result.isSuccess(), "Expected successful deletion of user profile by the owner");
//        Optional<UserProfile> deletedProfile = userProfileRepo.findByUsername(testUserProfile.getUsername());
//        assertTrue(deletedProfile.isEmpty(), "Expected profile to be deleted");
//    }
//
//    @AfterEach
//    public void tearDown() {
//        userProfileRepo.deleteById(testUserProfile.getId());
//        accountRepo.deleteById(testAccount.getId());
//    }
//
//}
