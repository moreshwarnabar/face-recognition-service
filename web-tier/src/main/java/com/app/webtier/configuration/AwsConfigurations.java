package com.app.webtier.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AwsConfigurations {

    private static final Logger LOG = LoggerFactory.getLogger(AwsConfigurations.class);

    /**
     * Create the ec2 client to start the ec2 instances
     *
     * @return The ec2 client
     */
    @Bean
    public Ec2Client ec2Client() {
        Ec2Client ec2Client = Ec2Client.builder()
                .region(Region.US_EAST_1)
                .build();
        LOG.info("Created the EC2 Client {}", ec2Client);

        return ec2Client;
    }

    /**
     * Create the S3 client to start the ec2 instances
     *
     * @return The S3 client
     */
    @Bean
    public S3Client s3Client() {
        S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .build();
        LOG.info("Created the EC2 Client {}", s3Client);

        return s3Client;
    }

    /**
     * Create the SQS client to start the ec2 instances
     *
     * @return The SQS client
     */
    @Bean
    public SqsClient sqsClient() {
        SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .build();
        LOG.info("Created the EC2 Client {}", sqsClient);

        return sqsClient;
    }

    /**
     * Create the instance request to run an ec2 instance
     *
     * @return The instance request to run an ec2 instance
     */

}
