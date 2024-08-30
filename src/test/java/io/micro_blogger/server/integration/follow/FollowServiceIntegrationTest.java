//package io.micro_blogger.server.integration.follow;
//
//import io.micro_blogger.server.common.CommonErrors;
//import io.micro_blogger.server.common.Result;
//import io.micro_blogger.server.integration.IntegrationTestBase;
//import io.micro_blogger.server.model.Account;
//import io.micro_blogger.server.model.Follow;
//import io.micro_blogger.server.repository.AccountRepo;
//import io.micro_blogger.server.repository.FollowRepo;
//import io.micro_blogger.server.service.follow.FollowService;
//import io.micro_blogger.server.viewmodel.FollowViewModel;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Transactional
//public class FollowServiceIntegrationTest extends IntegrationTestBase {
//
//    @Autowired
//    private FollowService followService;
//
//    @Autowired
//    private AccountRepo accountRepo;
//
//    @Autowired
//    private FollowRepo followRepo;
//
//    private Account follower;
//    private Account followee;
//
//    @BeforeEach
//    public void setUp() {
//        // Initialize and save follower
//        follower = new Account();
//        follower.setUsername("follower");
//        follower.setPassword("password");
//        follower = accountRepo.save(follower);  // Save the follower to ensure it gets an ID
//
//        // Initialize and save followee
//        followee = new Account();
//        followee.setUsername("followee");
//        followee.setPassword("password");
//        followee = accountRepo.save(followee);  // Save the followee to ensure it gets an ID
//
//        // Save a follow relationship if needed
//        Follow follow = new Follow();
//        follow.setFollowerId(follower.getId());
//        follow.setFolloweeId(followee.getId());
//        followRepo.save(follow);
//    }
//
//    @Test
//    public void testFollowUser_Success() {
//        Result<FollowViewModel> result = followService.followUser(follower.getUsername(), followee.getUsername());
//        assertTrue(result.isSuccess(), "Expected follow operation to succeed");
//
//        FollowViewModel followViewModel = result.getValue();
//        assertEquals(followee.getUsername(), followViewModel.getUsername(), "Followee username should match");
//
//        Follow follow = followRepo.findByFollowerIdAndFolloweeId(follower.getId(), followee.getId());
//        assertNotNull(follow, "Expected follow record to be created");
//    }
//
//    @Test
//    public void testFollowUser_AlreadyFollowing() {
//        // Ensure objects are initialized
//        assertNotNull(follower, "Follower should not be null");
//        assertNotNull(followee, "Followee should not be null");
//
//        // Create and save a follow relationship
//        followRepo.save(new Follow(follower.getId(), followee.getId()));
//        followRepo.flush(); // Ensure changes are committed
//
//        // Check if the follow relationship exists
//        boolean exists = followRepo.existsByFollowerIdAndFolloweeId(follower.getId(), followee.getId());
//        System.out.println("Follow relationship exists: " + exists);
//
//        // Attempt to follow again and check the result
//        Result<FollowViewModel> result = followService.followUser(follower.getUsername(), followee.getUsername());
//        System.out.println("Result isFailure: " + result.isFailure());
//        System.out.println("Result error: " + result.getError());
//
//        // Assert the result of the follow operation
//        assertTrue(result.isFailure(), "Expected follow operation to fail");
//        assertEquals("follow:already_followed", result.getError().getCode(), "Expected error code for already following");
//    }
//
//
//    @Test
//    public void testFollowUser_FollowSelf() {
//        Result<FollowViewModel> result = followService.followUser(follower.getUsername(), follower.getUsername());
//        assertTrue(result.isFailure(), "Expected follow operation to fail");
//
//        assertEquals(CommonErrors.UNAUTHORIZED_FOLLOW, result.getError(), "Expected error for attempting to follow oneself");
//    }
//
//    @Test
//    public void testUnfollowUser_Success() {
//        followRepo.save(new Follow(follower.getId(), followee.getId()));
//
//        Result<Void> result = followService.unfollowUser(follower.getUsername(), followee.getUsername());
//        assertTrue(result.isSuccess(), "Expected unfollow operation to succeed");
//
//        Follow follow = followRepo.findByFollowerIdAndFolloweeId(follower.getId(), followee.getId());
//        assertNull(follow, "Expected follow record to be deleted");
//    }
//
//    @Test
//    public void testUnfollowUser_NotFollowing() {
//        Result<Void> result = followService.unfollowUser(follower.getUsername(), followee.getUsername());
//        assertTrue(result.isFailure(), "Expected unfollow operation to fail");
//
//        assertEquals(CommonErrors.ENTITY_NOT_PRESENT, result.getError(), "Expected error for not following");
//    }
//
//    @Test
//    public void testGetFollowers_Success() {
//        followRepo.save(new Follow(follower.getId(), followee.getId()));
//
//        Result<List<String>> result = followService.getFollowers(followee.getUsername(), followee.getUsername());
//        assertTrue(result.isSuccess(), "Expected getFollowers operation to succeed");
//
//        List<String> followers = result.getValue();
//        assertTrue(followers.contains(follower.getUsername()), "Expected followers list to contain follower username");
//    }
//
//    @Test
//    public void testGetFollowers_AccessDenied() {
//        Result<List<String>> result = followService.getFollowers(follower.getUsername(), followee.getUsername());
//        assertTrue(result.isFailure(), "Expected getFollowers operation to fail");
//
//        assertEquals(CommonErrors.ACCESS_DENIED, result.getError(), "Expected access denied error");
//    }
//
//    @Test
//    public void testGetFollowees_Success() {
//        followRepo.save(new Follow(follower.getId(), followee.getId()));
//
//        Result<List<String>> result = followService.getFollowees(follower.getUsername(), follower.getUsername());
//        assertTrue(result.isSuccess(), "Expected getFollowees operation to succeed");
//
//        List<String> followees = result.getValue();
//        assertTrue(followees.contains(followee.getUsername()), "Expected followees list to contain followee username");
//    }
//
//    @Test
//    public void testGetFollowees_AccessDenied() {
//        Result<List<String>> result = followService.getFollowees(followee.getUsername(), follower.getUsername());
//        assertTrue(result.isFailure(), "Expected getFollowees operation to fail");
//
//        assertEquals(CommonErrors.ACCESS_DENIED, result.getError(), "Expected access denied error");
//    }
//}
