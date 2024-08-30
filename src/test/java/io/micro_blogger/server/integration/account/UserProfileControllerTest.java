//package io.micro_blogger.server.integration.account;
//
//import io.micro_blogger.server.integration.IntegrationTestBase;
//import io.micro_blogger.server.model.Account;
//import io.micro_blogger.server.model.UserProfile;
//import io.micro_blogger.server.repository.AccountRepo;
//import io.micro_blogger.server.repository.UserProfileRepo;
//import io.micro_blogger.server.service.security.TokenService;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//
//@Transactional
//@Rollback
//public class UserProfileControllerTest extends IntegrationTestBase {
//
//    @Autowired
//    private UserProfileRepo userProfileRepo;
//
//    @Autowired
//    private TokenService tokenService;
//
//    @Autowired
//    private AccountRepo accountRepo;
//
//    @Autowired
//    private WebApplicationContext webApplicationContext;
//
//    private MockMvc mockMvc;
//    private UserProfile testUserProfile;
//    private BCryptPasswordEncoder passwordEncoder;
//
//    @BeforeEach
//    public void setUp() {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
//                .apply(SecurityMockMvcConfigurers.springSecurity())
//                .build();
//
//        passwordEncoder = new BCryptPasswordEncoder();
//
//        // Create and save the test account
//        Account testAccount = new Account();
//        testAccount.setUsername("testUser");
//        testAccount.setPassword(passwordEncoder.encode("password"));
//        accountRepo.save(testAccount); // This should persist and generate a UUID for testAccount
//
//        // Create and save the test user profile
//        testUserProfile = new UserProfile(testAccount);
//        testUserProfile.setEmail("test@example.com");
//        testUserProfile.setBio("Test biography");
//        testUserProfile.setAvatar("http://example.com/avatar.jpg");
//        testUserProfile.setRegistrationDate(LocalDateTime.now());
//        testUserProfile.setUsername("testUser");
//        userProfileRepo.save(testUserProfile);
//
//        // Set up authentication with UUID
//        UsernamePasswordAuthenticationToken auth =
//                new UsernamePasswordAuthenticationToken(testAccount.getId().toString(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
//        SecurityContextHolder.getContext().setAuthentication(auth);
//    }
//
//
//    @Test
//    public void testGetUserProfile() throws Exception {
//        mockMvc.perform(get("/users/{username}", testUserProfile.getUsername()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.username").value(testUserProfile.getUsername()));
//    }
//
//    @Test
//    @WithMockUser(username = "testUser", roles = {"USER"})
//    public void testUpdateUserProfile() throws Exception {
//        String requestJson = "{\"biography\":\"Updated biography\", \"email\":\"test@example.com\"}";
//
//        // Perform the PATCH request
//        mockMvc.perform(patch("/users/{username}/userDetails", testUserProfile.getUsername())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestJson))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.biography").value("Updated biography"))
//                .andExpect(jsonPath("$.username").value(testUserProfile.getUsername()))
//                .andExpect(jsonPath("$.email").value("test@example.com"));
//    }
//
//    @WithMockUser(username = "testUser", roles = {"USER"})
//    @Test
//    public void testUploadAvatar() throws Exception {
//        String username = "testUser";
//        MockMultipartFile avatarFile = new MockMultipartFile("avatar", "avatar.jpg", "image/jpeg", "some image content".getBytes());
//
//        mockMvc.perform(multipart("/users/{username}/avatar", username)
//                        .file(avatarFile)
//                        .contentType(MediaType.MULTIPART_FORM_DATA)
//                        .principal(new UsernamePasswordAuthenticationToken(username, "password")))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser(username = "testUser", roles = {"USER"})
//    public void testDeleteUserProfile() throws Exception {
//        mockMvc.perform(delete("/users/{username}", testUserProfile.getUsername()))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    @WithMockUser(username = "testUser", roles = {"USER"})
//    public void testSearchUserProfiles() throws Exception {
//        Account otherAccount = new Account();
//        otherAccount.setUsername("otherUser");
//        otherAccount.setPassword(passwordEncoder.encode("password"));
//        accountRepo.save(otherAccount);
//
//        UserProfile otherUserProfile = new UserProfile(otherAccount);
//        otherUserProfile.setEmail("other@example.com");
//        otherUserProfile.setBio("Other user biography");
//        otherUserProfile.setAvatar("http://example.com/otherAvatar.jpg");
//        otherUserProfile.setRegistrationDate(LocalDateTime.now());
//        otherUserProfile.setUsername("otherUser");
//        userProfileRepo.save(otherUserProfile);
//
//        mockMvc.perform(get("/users/search").param("username", "otherUser"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].username").value("otherUser"))
//                .andExpect(jsonPath("$.length()").value(1));
//    }
//
//    @Test
//    public void testGetUserProfileNotFound() throws Exception {
//        mockMvc.perform(get("/users/{username}", "nonExistentUser"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @WithMockUser(username = "testUser", roles = {"USER"})
//    public void testUpdateUserProfileNotFound() throws Exception {
//        String requestJson = "{\"biography\":\"Valid bio\", \"username\":\"nonExistentUser\", \"email\":\"nonexistent@example.com\"}";
//        mockMvc.perform(patch("/users/{username}/userDetails", "nonExistentUser")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestJson))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    public void testDeleteNonExistentUserProfile() throws Exception {
//        mockMvc.perform(delete("/users/{username}", "nonExistentUser"))
//                .andExpect(status().isNotFound());
//    }
//}
