//package io.micro_blogger.server.integration.follow;
//
//import io.micro_blogger.server.dto.FollowUserRequest;
//import io.micro_blogger.server.integration.IntegrationTestBase;
//import io.micro_blogger.server.model.Account;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//public class FollowControllerIntegrationTest extends IntegrationTestBase {
//
//    @Test
//    public void testFollowUser() throws Exception {
//        String followerUsername = "user1";
//        String followeeUsername = "user2";
//
//        Account follower = createUniqueAccount(followerUsername);
//        String authHeader = authHeader(follower);
//
//        FollowUserRequest followUserRequest = new FollowUserRequest(followeeUsername);
//
//        mockMvc.perform(MockMvcRequestBuilders.post(endpointUrl("/users/follow"))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("Authorization", authHeader)
//                        .content(toJson(followUserRequest)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void testUnfollowUser() throws Exception {
//        String followerUsername = "user1";
//        String followeeUsername = "user2";
//
//        // Create and authenticate follower account
//        Account follower = createUniqueAccount(followerUsername);
//        String authHeader = authHeader(follower);
//
//        // Make sure the follower follows the followee first
//        // Follow the user before unfollowing
//        FollowUserRequest followUserRequest = new FollowUserRequest(followeeUsername);
//        mockMvc.perform(MockMvcRequestBuilders.post(endpointUrl("/users/follow"))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("Authorization", authHeader)
//                        .content(toJson(followUserRequest)))
//                .andExpect(status().isOk());
//
//        // Unfollow user
//        mockMvc.perform(MockMvcRequestBuilders.delete(endpointUrl("/users/" + followeeUsername + "/follow"))
//                        .header("Authorization", authHeader))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void testGetFollowers() throws Exception {
//        String username = "user2";
//
//        // Create and authenticate user account
//        Account user = createUniqueAccount(username);
//        String authHeader = authHeader(user);
//
//        // Create a follower account
//        Account follower = createUniqueAccount("user1");
//        String followerAuthHeader = authHeader(follower);
//
//        // Follow the user
//        FollowUserRequest followUserRequest = new FollowUserRequest(username);
//        mockMvc.perform(MockMvcRequestBuilders.post(endpointUrl("/users/follow"))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("Authorization", followerAuthHeader)
//                        .content(toJson(followUserRequest)))
//                .andExpect(status().isOk());
//
//        // Retrieve followers
//        mockMvc.perform(MockMvcRequestBuilders.get(endpointUrl("/users/" + username + "/followers"))
//                        .header("Authorization", authHeader))
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
//    }
//
//    @Test
//    public void testGetFollowees() throws Exception {
//        String username = "user1";
//
//        // Create and authenticate user account
//        Account user = createUniqueAccount(username);
//        String authHeader = authHeader(user);
//
//        // Create a followee account
//        Account followee = createUniqueAccount("user2");
//
//        // Follow the followee
//        FollowUserRequest followUserRequest = new FollowUserRequest(followee.getUsername());
//        mockMvc.perform(MockMvcRequestBuilders.post(endpointUrl("/users/follow"))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("Authorization", authHeader)
//                        .content(toJson(followUserRequest)))
//                .andExpect(status().isOk());
//
//        // Retrieve followees
//        mockMvc.perform(MockMvcRequestBuilders.get(endpointUrl("/users/" + username + "/followees"))
//                        .header("Authorization", authHeader))
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
//    }
//}
