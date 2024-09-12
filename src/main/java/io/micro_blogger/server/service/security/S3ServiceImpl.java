package io.micro_blogger.server.service.security;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import io.micro_blogger.server.common.ApiError;
import io.micro_blogger.server.common.CommonErrors;
import io.micro_blogger.server.common.Result;
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
    public Result<String> uploadImage(MultipartFile imageFile) {
        if (imageFile.isEmpty()) {
            return Result.failure(CommonErrors.NULL_IMAGE_FILE);
        }

        if (imageFile.getSize() > MAX_FILE_SIZE) {
            return Result.failure(new ApiError("file:too_large", "File size exceeds limit of " + MAX_FILE_SIZE + " bytes."));
        }

        String contentType = imageFile.getContentType();
        if (!isValidImageType(contentType)) {
            return Result.failure(new ApiError("image:invalid_type", "Invalid image type: " + contentType));
        }

        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

        try (InputStream inputStream = imageFile.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(imageFile.getSize());
            metadata.setContentType(contentType);

            PutObjectRequest request = new PutObjectRequest(bucketName, fileName, inputStream, metadata);
            amazonS3.putObject(request);

            String fileUrl = amazonS3.getUrl(bucketName, fileName).toString();
            return Result.success(fileUrl);
        } catch (IOException e) {
            return Result.failure(new ApiError("s3:upload_error", "Error uploading file: " + e.getMessage()));
        } catch (Exception e) {
            return Result.failure(new ApiError("s3:general_error", "Unexpected error occurred: " + e.getMessage()));
        }
    }

    private boolean isValidImageType(String contentType) {
        return contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png"));
    }
}
