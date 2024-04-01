package com.app.webtier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

@Service
public class S3Service implements IS3Service {

    private static final Logger LOG = LoggerFactory.getLogger(S3Service.class);

    private static final String IMAGE_BUCKET = "1229975385-in-bucket";

    private final S3Client s3Client;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public boolean saveImage(String key, MultipartFile file) {
        boolean isSaved = false;
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(IMAGE_BUCKET)
                    .key(key)
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
            LOG.info("Stored {} into the S3 Image Bucket!", key);
            isSaved = true;
        } catch (IOException | S3Exception e) {
            LOG.error(e.getMessage());
        }
        return isSaved;
    }

}
