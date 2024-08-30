package io.micro_blogger.server.service.follow;

import io.micro_blogger.server.common.CommonErrors;
import io.micro_blogger.server.common.Result;
import io.micro_blogger.server.model.Account;
import io.micro_blogger.server.model.Follow;
import io.micro_blogger.server.model.UserProfile;
import io.micro_blogger.server.repository.AccountRepo;
import io.micro_blogger.server.repository.FollowRepo;
import io.micro_blogger.server.repository.UserProfileRepo;
import io.micro_blogger.server.service.post.PostService;
import io.micro_blogger.server.viewmodel.FollowViewModel;
import io.micro_blogger.server.viewmodel.PostViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private FollowRepo followRepo;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private UserProfileRepo userProfileRepo;

    @Autowired
    private PostService postService;

    @Override
    @Transactional
    public Result<FollowViewModel> followUser(String followerUsername, String followeeUsername) {
        if (followerUsername.equals(followeeUsername)) {
            return Result.failure(CommonErrors.UNAUTHORIZED_FOLLOW);
        }

        Account follower = getAccountByUsername(followerUsername);
        Account followee = getAccountByUsername(followeeUsername);

        if (follower == null || followee == null) {
            return Result.failure(CommonErrors.ENTITY_NOT_PRESENT);
        }

        if (followRepo.existsByFollowerIdAndFolloweeId(follower.getId(), followee.getId())) {
            return Result.failure(CommonErrors.ALREADY_FOLLOWING);
        }

        followRepo.save(new Follow(follower.getId(), followee.getId()));
        updateUserProfileCounts(follower.getId(), followee.getId());

        FollowViewModel result = buildFollowViewModel(followee);
        return Result.success(result);
    }

    @Override
    @Transactional
    public Result<Void> unfollowUser(String followerUsername, String followeeUsername) {
        Account follower = getAccountByUsername(followerUsername);
        Account followee = getAccountByUsername(followeeUsername);

        if (follower == null || followee == null) {
            return Result.failure(CommonErrors.ENTITY_NOT_PRESENT);
        }

        Follow follow = followRepo.findByFollowerIdAndFolloweeId(follower.getId(), followee.getId());
        if (follow == null) {
            return Result.failure(CommonErrors.ENTITY_NOT_PRESENT);
        }

        followRepo.delete(follow);
        updateUserProfileCounts(follower.getId(), followee.getId());

        return Result.success(null);
    }

    @Override
    public Result<List<String>> getFollowers(String requestingUsername, String followeeUsername) {
        return getUsernamesList(requestingUsername, followeeUsername, true);
    }

    @Override
    public Result<List<String>> getFollowees(String requestingUsername, String followeeUsername) {
        return getUsernamesList(requestingUsername, followeeUsername, false);
    }

    @Override
    public boolean isFollower(UUID followerId, UUID followedId) {
        return followRepo.existsByFollowerIdAndFolloweeId(followerId, followedId);
    }

    private Account getAccountByUsername(String username) {
        return accountRepo.findByUsername(username).orElse(null);
    }

    private Result<List<String>> getUsernamesList(String requestingUsername, String followeeUsername, boolean isFollowers) {
        Account requestingUser = getAccountByUsername(requestingUsername);
        Account followee = getAccountByUsername(followeeUsername);

        if (requestingUser == null || followee == null) {
            return Result.failure(CommonErrors.ENTITY_NOT_PRESENT);
        }

        boolean isFollowing = followRepo.existsByFollowerIdAndFolloweeId(requestingUser.getId(), followee.getId());

        if (!isFollowing) {
            return Result.failure(CommonErrors.ACCESS_DENIED);
        }

        List<String> usernames = getUsernames(
                isFollowers ? followRepo.findByFolloweeId(followee.getId()) : followRepo.findByFollowerId(followee.getId()),
                isFollowers
        );

        return Result.success(usernames);
    }

    private List<String> getUsernames(List<Follow> follows, boolean isFollowerList) {
        return follows.stream()
                .map(follow -> {
                    UUID userId = isFollowerList ? follow.getFollowerId() : follow.getFolloweeId();
                    return accountRepo.findById(userId)
                            .map(Account::getUsername)
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private FollowViewModel buildFollowViewModel(Account followee) {
        int followerCount = followRepo.countByFolloweeId(followee.getId());
        int followeeCount = followRepo.countByFollowerId(followee.getId());

        List<String> followerUsernames = getUsernames(followRepo.findByFolloweeId(followee.getId()), true);
        List<PostViewModel> recentPosts = postService.getTopRecentPostsByAccount(followee.getId());

        FollowViewModel result = new FollowViewModel(followee.getUsername());
        result.setFollowerCount(followerCount);
        result.setFolloweeCount(followeeCount);
        result.setFollowerUsernames(followerUsernames);
        result.setRecentPosts(recentPosts);

        return result;
    }

    private void updateUserProfileCounts(UUID followerId, UUID followeeId) {
        Optional<UserProfile> followerProfileOpt = userProfileRepo.findById(followerId);
        Optional<UserProfile> followeeProfileOpt = userProfileRepo.findById(followeeId);

        followerProfileOpt.ifPresent(profile -> {
            profile.setFolloweeCount(followRepo.countByFollowerId(profile.getId()));
            userProfileRepo.save(profile);
        });

        followeeProfileOpt.ifPresent(profile -> {
            profile.setFollowerCount(followRepo.countByFolloweeId(profile.getId()));
            userProfileRepo.save(profile);
        });
    }
}
