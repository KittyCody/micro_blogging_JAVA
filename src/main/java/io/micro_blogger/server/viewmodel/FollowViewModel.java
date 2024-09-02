package io.micro_blogger.server.viewmodel;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
public class FollowViewModel {
    private String followeeUsername;
    private int followerCount;
    private int followeeCount;
    private List<String> followerUsernames;
    private List<PostViewModel> recentPosts;

    public FollowViewModel(String followeeUsername) {
        this.followeeUsername = followeeUsername;
    }
}
