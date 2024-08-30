package io.micro_blogger.server.service.security;
import org.springframework.web.multipart.MultipartFile;
import io.micro_blogger.server.common.Result;

public interface S3Service {
    Result<String> uploadImage(MultipartFile imageFile);
}
