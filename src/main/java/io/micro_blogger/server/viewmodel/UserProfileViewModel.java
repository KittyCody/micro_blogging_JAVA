package io.micro_blogger.server.viewmodel;
import java.time.LocalDateTime;

public record UserProfileViewModel(
        String avatar,
        String username,
        String email,
        String biography,
        int followerCount,
        int followeeCount,
        LocalDateTime registrationDate
) {
}
