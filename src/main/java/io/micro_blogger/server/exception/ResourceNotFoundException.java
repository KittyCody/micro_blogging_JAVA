package io.micro_blogger.server.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException accountNotFound(String accountId) {
        return new ResourceNotFoundException("Account not found with id: " + accountId);
    }

    public static ResourceNotFoundException postNotFound(Long postId) {
        return new ResourceNotFoundException("Post not found with ID: " + postId);
    }
}
