package com.example.resourceservice.service;

import com.example.resourceservice.exception.StorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3StorageService {

    private static final String KEY_PREFIX = "resources/";
    private static final String KEY_SUFFIX = ".mp3";

    private final S3Client s3Client;

    public String upload(byte[] data, String bucket) {
        String key = KEY_PREFIX + UUID.randomUUID() + KEY_SUFFIX;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType("audio/mpeg")
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(data));
            return key;
        } catch (S3Exception e) {
            throw new StorageException("Failed to upload file to cloud storage", e);
        }
    }

    public byte[] download(String key, String bucket) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            try (ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request)) {
                return response.readAllBytes();
            }
        } catch (S3Exception e) {
            throw new StorageException("Failed to download file from cloud storage", e);
        } catch (IOException e) {
            throw new StorageException("Failed to read file content from cloud storage", e);
        }
    }

    public void delete(String key, String bucket) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(request);
        } catch (S3Exception e) {
            throw new StorageException("Failed to delete file from cloud storage", e);
        }
    }

    public void move(String key, String sourceBucket, String destinationBucket) {
        try {
            CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                    .sourceBucket(sourceBucket)
                    .sourceKey(key)
                    .destinationBucket(destinationBucket)
                    .destinationKey(key)
                    .build();

            s3Client.copyObject(copyRequest);
            delete(key, sourceBucket);
        } catch (S3Exception e) {
            throw new StorageException("Failed to move file between storage locations", e);
        }
    }
}