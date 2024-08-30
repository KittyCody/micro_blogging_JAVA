package io.micro_blogger.server.dto;

public record FollowUserRequest(String username) {


    public String getUsername() {
        return username;
    }

    public void setUsername(String followeeUsername) {
    }
}
