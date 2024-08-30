package io.micro_blogger.server.common;

public class CommonErrors {

    public static final ApiError ENTITY_NOT_PRESENT = new ApiError("entity:not_present", "Entity is not present");

    public static final ApiError ACCOUNT_ALREADY_EXISTS = new ApiError("account:already_exists", "Account already exists");

    public static final ApiError ACCOUNT_CREDENTIALS_MISMATCH = new ApiError("account:credentials_mismatch", "Account credentials do not match");

    public static final ApiError ALREADY_FOLLOWING = new ApiError("follow:already_followed", "Account already followed");

    public static final ApiError USERNAME_INVALID = new ApiError("username:invalid_username", "Invalid username");

    public static final ApiError PASSWORD_TOO_SHORT = new ApiError("password:too short", "Password too short");

    public static final ApiError UNAUTHORIZED_FOLLOW = new ApiError("follow:unauthorized_follow", "Cannot follow yourself");

    public static final ApiError ACCESS_DENIED = new ApiError("access_denied", "Access denied. Unauthorized") ;

    public static final ApiError DELETE_FORBIDDEN = new ApiError("forbidden:delete_forbidden", "You cannot delete another user's post");

    public static final ApiError IMAGE_UPLOAD_FAILED = new ApiError("image:upload_failed", "Failed to upload image");

    public static final ApiError NULL_IMAGE_FILE = new ApiError("image:null_image", "The image file must not be null or empty");

    public static final ApiError BAD_REQUEST = new ApiError("word:null_keyword", "Key word cannot be empty.");

    public static final ApiError FORBIDDEN_OPERATION = new ApiError("post:unauthorized_view", "You are not authorized to perform that operation.");

    public static final ApiError INVALID_UUID = new ApiError("uuid:invalid_uuid", "Invalid UUID string");

    public static final ApiError AVATAR_UPLOAD_FAILED = new ApiError("avatar:upload_failed", "Failed to upload avatar");

}
