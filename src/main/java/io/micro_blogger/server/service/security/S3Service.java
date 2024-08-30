package io.micro_blogger.server.service.security;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {
    String uploadImage(MultipartFile imageFile) throws IOException;
}