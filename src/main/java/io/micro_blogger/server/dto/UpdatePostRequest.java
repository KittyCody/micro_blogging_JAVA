package io.micro_blogger.server.dto;
import java.util.Set;

public record UpdatePostRequest(String description, Set<String> tags, String imageUrl) {
}
