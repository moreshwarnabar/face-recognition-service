package com.app.apptier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

@Service
public class S3Service implements IS3Service {

    private static final Logger LOG = LoggerFactory.getLogger(S3Service.class);

    private static final String IMAGE_BUCKET = "1229975385-in-bucket";

    private static final String RESULT_BUCKET = "1229975385-out-bucket";

    private final S3Client s3Client;

    S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public Path downloadImage(String imageName) {
        String filePath = String.format("%s.jpg", imageName);
        try (OutputStream os = new FileOutputStream(filePath)) {
            GetObjectRequest request = GetObjectRequest.builder()
                    .key(imageName)
                    .bucket(IMAGE_BUCKET)
                    .build();

            ResponseBytes<GetObjectResponse> bytes = s3Client.getObjectAsBytes(request);
            byte[] data = bytes.asByteArray();

            os.write(data);
            LOG.info("Downloaded {} from s3 image bucket", imageName);
            return Path.of(filePath);
        } catch (IOException | S3Exception e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveResult(String imageName, String result) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .key(imageName)
                    .bucket(RESULT_BUCKET)
                    .build();

            s3Client.putObject(request, RequestBody.fromString(result));
            LOG.info("Saved result {}, for image {}, into s3 result bucket", result, imageName);
        } catch (S3Exception e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
