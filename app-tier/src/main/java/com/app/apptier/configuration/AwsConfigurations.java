package com.app.apptier.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AwsConfigurations {

    private static final Logger LOG = LoggerFactory.getLogger(AwsConfigurations.class);

    @Bean
    public S3Client s3Client() {
        S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .build();
        LOG.info("Created the S3 Client {}", s3Client);

        return s3Client;
    }

    @Bean
    public SqsClient sqsClient() {
        SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .build();
        LOG.info("Created the SQS Client {}", sqsClient);

        return sqsClient;
    }

}