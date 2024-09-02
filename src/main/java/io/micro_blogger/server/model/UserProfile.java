package io.micro_blogger.server.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
public class UserProfile {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private UUID id;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "bio")
    private String bio;

    private String avatar;

    @Column(nullable = false, unique = true)
    @NotNull(message = "Username cannot be null")
    private String username;

    @Column(unique = true)
    private String email;

    @Column(name = "date_of_registration")
    private LocalDateTime registrationDate;

    @Column(name = "follower_count", nullable = false)
    private int followerCount = 0;

    @Column(name = "followee_count", nullable = false)
    private int followeeCount = 0;

    public UserProfile(Account account) {
        this.id = account.getId();
        this.account = account;
        this.bio = "";
        this.username = account.getUsername();
        this.registrationDate = LocalDateTime.now();
    }

    public UserProfile() {
    }
}
