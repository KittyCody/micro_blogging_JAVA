package io.micro_blogger.server.repository;

import io.micro_blogger.server.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepo extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByUsername(String username);

    @Query("SELECT u FROM UserProfile u WHERE u.username ILIKE %:username%")
    List<UserProfile> findByUsernameFuzzy(String username);
}
