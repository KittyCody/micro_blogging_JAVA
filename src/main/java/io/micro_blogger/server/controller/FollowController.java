package io.micro_blogger.server.controller;

import io.micro_blogger.server.common.CommonErrors;
import io.micro_blogger.server.common.Result;
import io.micro_blogger.server.dto.FollowUserRequest;
import io.micro_blogger.server.service.follow.FollowService;
import io.micro_blogger.server.viewmodel.FollowViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class FollowController {

    @Autowired
    private FollowService followService;

    @PostMapping("/follow")
    public ResponseEntity<?> followUser(
            @RequestBody FollowUserRequest followUserRequest,
            @AuthenticationPrincipal Jwt jwt) {

        String followerUsername = getUsernameFromJwt(jwt);
        String followeeUsername = followUserRequest.getUsername();

        Result<FollowViewModel> result = followService.followUser(followerUsername, followeeUsername);
        return buildResponse(result);
    }

    @DeleteMapping("/{username}/follow")
    public ResponseEntity<?> unfollowUser(
            @PathVariable String username,
            @AuthenticationPrincipal Jwt jwt) {

        String followerUsername = getUsernameFromJwt(jwt);

        Result<Void> result = followService.unfollowUser(followerUsername, username);
        return buildResponse(result);
    }

    @GetMapping("/{username}/followers")
    public ResponseEntity<?> getFollowers(
            @PathVariable String username,
            @AuthenticationPrincipal Jwt jwt) {

        String requestingUsername = getUsernameFromJwt(jwt);

        Result<List<String>> result = followService.getFollowers(requestingUsername, username);
        return buildResponse(result);
    }

    @GetMapping("/{username}/followees")
    public ResponseEntity<?> getFollowees(
            @PathVariable String username,
            @AuthenticationPrincipal Jwt jwt) {

        String requestingUsername = getUsernameFromJwt(jwt);

        Result<List<String>> result = followService.getFollowees(requestingUsername, username);
        return buildResponse(result);
    }

    private String getUsernameFromJwt(Jwt jwt) {
        if (jwt == null || !jwt.getClaims().containsKey("sub")) {
            throw new IllegalStateException("JWT claims missing");
        }
        return jwt.getClaim("username");
    }

    private ResponseEntity<?> buildResponse(Result<?> result) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getValue());
        }

        HttpStatus status;
        if (result.getError() == CommonErrors.ENTITY_NOT_PRESENT) {
            status = HttpStatus.NOT_FOUND;
        } else if (result.getError() == CommonErrors.USERNAME_INVALID) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (result.getError() == CommonErrors.UNAUTHORIZED_FOLLOW) {
            status = HttpStatus.FORBIDDEN;
        } else if (result.getError() == CommonErrors.ACCESS_DENIED) {
            status = HttpStatus.FORBIDDEN;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(result.getError(), status);
    }
}
