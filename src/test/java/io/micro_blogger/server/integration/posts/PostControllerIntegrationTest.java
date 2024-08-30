//package io.micro_blogger.server.integration.posts;
//
//import io.micro_blogger.server.dto.CreatePostRequest;
//import io.micro_blogger.server.dto.UpdatePostRequest;
//import io.micro_blogger.server.integration.IntegrationTestBase;
//import io.micro_blogger.server.model.Account;
//import io.micro_blogger.server.model.CustomUserDetails;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
//import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.transaction.annotation.Transactional;
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@Transactional
//@ActiveProfiles("test")
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class PostControllerIntegrationTest extends IntegrationTestBase {
//
//    @BeforeEach
//    public void beforeEach() {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
//                .apply(SecurityMockMvcConfigurers.springSecurity()) // Apply Spring Security configuration
//                .build();
//    }
//
//    @Test
//    public void testCreatePost() throws Exception {
//        UUID accountId = UUID.randomUUID(); // The account ID in the URL
//        UUID userId = UUID.randomUUID(); // The mock user ID for authentication
//
//        // Debug: Print UUIDs for comparison
//        System.out.println("accountId: " + accountId);
//        System.out.println("userId: " + userId);
//
//        // Prepare multipart file
//        MockMultipartFile file = new MockMultipartFile("imageFile", "test-image.jpg", "image/jpeg", "test image content".getBytes());
//
//        // Prepare post request payload as a JSON string
//        CreatePostRequest createRequest = new CreatePostRequest("Post Title", Set.of("tag1", "tag2"));
//        String createRequestJson = toJson(createRequest);
//
//        // Convert JSON payload to MultipartFile
//        MockMultipartFile postPart = new MockMultipartFile("post", "", "application/json", createRequestJson.getBytes());
//
//        // Create mock Account using the no-argument constructor and setters
//        Account account = new Account();
//        account.setId(userId); // Set the UUID for the Account
//        account.setUsername("testuser");
//        account.setPassword("password");
//
//        // Create CustomUserDetails with the mock Account
//        CustomUserDetails userDetails = new CustomUserDetails(account);
//
//        // Set up the Security Context with the CustomUserDetails
//        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
//        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
//        securityContext.setAuthentication(authentication);
//        SecurityContextHolder.setContext(securityContext);
//
//        // Perform the multipart request with authentication
//        mockMvc.perform(MockMvcRequestBuilders.multipart("/posts/accounts/" + accountId + "/posts")
//                        .file(file)  // File upload
//                        .file(postPart) // Post data
//                        .contentType(MediaType.MULTIPART_FORM_DATA))
//                .andExpect(MockMvcResultMatchers.status().isCreated());
//    }
//
//    @Test
//    public void testUpdatePost() throws Exception {
//        UUID accountId = UUID.randomUUID();
//        UUID postId = UUID.randomUUID();
//        String authHeader = authHeader(accountId, "testuser");
//
//        // Correctly using Set<String> for tags
//        UpdatePostRequest updateRequest = new UpdatePostRequest("Updated Title", Set.of("tag1", "tag2"), "http://example.com/newimage.jpg");
//        String requestJson = toJson(updateRequest);
//
//        mockMvc.perform(patch(endpointUrl("/posts/accounts/" + accountId + "/" + postId))
//                        .content(requestJson)
//                        .header("Authorization", authHeader)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.description").value("Updated Title"))
//                .andExpect(jsonPath("$.tags[0]").value("tag1"))
//                .andExpect(jsonPath("$.tags[1]").value("tag2"))
//                .andExpect(jsonPath("$.imageUrl").value("http://example.com/newimage.jpg"));
//    }
//
//    @Test
//    public void testGetPostsByAccount() throws Exception {
//        UUID accountId = UUID.randomUUID();
//        String authHeader = authHeader(accountId, "testuser");
//
//        mockMvc.perform(get(endpointUrl("/posts/accounts/" + accountId + "/posts"))
//                        .header("Authorization", authHeader))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void testGetPostsByTagsContaining() throws Exception {
//        UUID accountId = UUID.randomUUID();
//        String authHeader = authHeader(accountId, "testuser");
//
//        mockMvc.perform(get(endpointUrl("/posts/accounts/" + accountId + "/tags")
//                        + "?tag=tag1")
//                        .header("Authorization", authHeader))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void testSearchPosts() throws Exception {
//        UUID accountId = UUID.randomUUID();
//        String authHeader = authHeader(accountId, "testuser");
//
//        mockMvc.perform(get(endpointUrl("/posts/accounts/" + accountId + "/search")
//                        + "?keyword=test")
//                        .header("Authorization", authHeader))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void testGetTopRecentPosts() throws Exception {
//        mockMvc.perform(get(endpointUrl("/posts/recent")))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void testPaginatedPosts() throws Exception {
//        UUID accountId = UUID.randomUUID();
//        String authHeader = authHeader(accountId, "testuser");
//
//        mockMvc.perform(get(endpointUrl("/posts/accounts/" + accountId + "/paginated")
//                        + "?page=0&size=10")
//                        .header("Authorization", authHeader))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void testDeletePost() throws Exception {
//        UUID accountId = UUID.randomUUID();
//        UUID postId = UUID.randomUUID();
//        String authHeader = authHeader(accountId, "testuser");
//
//        mockMvc.perform(delete(endpointUrl("/posts/accounts/" + accountId + "/" + postId))
//                        .header("Authorization", authHeader))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    public void testGetPostById() throws Exception {
//        UUID accountId = UUID.randomUUID();
//        UUID postId = UUID.randomUUID();
//        String authHeader = authHeader(accountId, "testuser");
//
//        // Create a post and account before testing retrieval
//        // Create an account
//        Account account = createUniqueAccount("testuser");
//        account.setId(accountId);
//        tokenService.generate(account); // Make sure this step is correct for generating token
//
//        // Create a post
//        CreatePostRequest createRequest = new CreatePostRequest("Post Title", Set.of("tag1", "tag2"));
//        String createRequestJson = toJson(createRequest);
//
//        mockMvc.perform(post(endpointUrl("/posts/accounts/" + accountId + "/posts"))
//                        .header("Authorization", authHeader)
//                        .content(createRequestJson)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated());
//
//        // Retrieve the post by ID
//        mockMvc.perform(get(endpointUrl("/posts/accounts/" + accountId + "/" + postId))
//                        .header("Authorization", authHeader))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(postId.toString())); // Adjust expected response as needed
//    }
//
//}
