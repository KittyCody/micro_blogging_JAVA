package io.micro_blogger.server.service.security;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 amazonS3;
    private final String bucketName;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Autowired
    public S3ServiceImpl(AmazonS3 amazonS3, @Value("${cloud.aws.s3.bucket}") String bucketName) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
    }

    @Override
    public String uploadImage(MultipartFile imageFile) throws IOException {
        if (imageFile.getSize() > MAX_FILE_SIZE) {
            throw new IOException("File size exceeds limit of " + MAX_FILE_SIZE + " bytes.");
        }

        String contentType = imageFile.getContentType();
        if (!isValidImageType(contentType)) {
            throw new IOException("Invalid image type: " + contentType);
        }

        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

        try (InputStream inputStream = imageFile.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(imageFile.getSize());
            metadata.setContentType(contentType);

            PutObjectRequest request = new PutObjectRequest(bucketName, fileName, inputStream, metadata);
            amazonS3.putObject(request);
        } catch (AmazonServiceException e) {
            throw new IOException("Error uploading file to S3: " + e.getMessage(), e);
        }

        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    private boolean isValidImageType(String contentType) {
        return contentType.equals("image/jpeg") || contentType.equals("image/png");
    }
}
