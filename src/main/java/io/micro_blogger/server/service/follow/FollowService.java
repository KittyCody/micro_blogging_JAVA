package io.micro_blogger.server.service.follow;

import io.micro_blogger.server.common.Result;
import io.micro_blogger.server.viewmodel.FollowViewModel;
import java.util.List;
import java.util.UUID;

public interface FollowService {
    Result<FollowViewModel> followUser(String followerUsername, String followeeUsername);

    Result<Void> unfollowUser(String followerUsername, String followeeUsername);

    Result<List<String>> getFollowers(String requestingUsername, String followeeUsername);

    Result<List<String>> getFollowees(String requestingUsername, String followeeUsername);

    boolean isFollower(UUID followerId, UUID followedId);
}
