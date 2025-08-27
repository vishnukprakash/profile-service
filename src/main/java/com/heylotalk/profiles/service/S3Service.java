package com.heylotalk.profiles.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String uploadImage(Long userId, byte[] imageBytes, String contentType) {
        String key = generateKey(userId);
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(imageBytes.length);

            PutObjectRequest request = new PutObjectRequest(
                    bucketName,
                    key,
                    new ByteArrayInputStream(imageBytes),
                    metadata
            );

            amazonS3.putObject(request);
            return key;
        } catch (AmazonServiceException e) {
            log.error("Error uploading image to S3", e);
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    private String generateKey(Long userId) {
        return String.format("profile-images/%d/%s", userId, UUID.randomUUID());
    }

    public String generatePresignedUrl(String key) {
        Date expiration = new Date(System.currentTimeMillis() + (15 * 60 * 1000)); // 30 seconds expiration
        return amazonS3.generatePresignedUrl(bucketName, key, expiration).toString();
    }
}
