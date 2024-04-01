package com.app.apptier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.Message;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AppTierService implements IAppTierService {

    private static final Logger LOG = LoggerFactory.getLogger(AppTierService.class);
    private final S3Service s3Service;
    private final SqsService sqsService;

    AppTierService(S3Service s3Service, SqsService sqsService) {
        this.s3Service = s3Service;
        this.sqsService = sqsService;
    }

    @Scheduled(fixedDelay = 2000)
    public void scheduleImageClassification() {
        LOG.info("Checking for requests to classify images at: {}", new Date(System.currentTimeMillis()));
        try {
            processImageClassification();
        } catch (Exception e) {
            LOG.error("Encountered error! Stopping this EC2 instance!");
            stopEc2Instance();
        }
    }

    @Override
    public void processImageClassification() {
        try {
            // read message from SQS request queue
            Message message = sqsService.receiveMessage();
            String imageName = message.body();
            // download the image from the S3 images bucket
            Path path = s3Service.downloadImage(imageName);
            // use the image classification model to recognize the image
            String result = getClassificationResult(path);
            // persist the result in the S3 results bucket
            s3Service.saveResult(imageName, result);
            // add the result into the SQS result queue
            String body = String.format("%s:%s", imageName, result);
            sqsService.sendMessage(body);
            // delete the request from the SQS request queue
            sqsService.deleteMessage(message);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stopEc2Instance() {
        String command = "sudo shutdown -h now";
        LOG.info("Running command: {}", command);
        ProcessBuilder processBuilder = new ProcessBuilder()
                .command("sh", "-c", command);

        try {
            processBuilder.start();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        System.exit(0);
    }

    private String getClassificationResult(Path path) {
        try {
            // run face recognition script to classify the image
            String command = String.format("python3 face_recognition.py %s", path.toAbsolutePath());
            LOG.info("Running command: {}", command);
            ProcessBuilder processBuilder = new ProcessBuilder()
                    .command("sh", "-c", command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            boolean isCompleted = process.waitFor(120, TimeUnit.SECONDS);
            String result = "";
            if (isCompleted) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                List<String> output = br.lines().toList();
                result = output.get(output.size() - 1);
                br.close();
            } else {
                LOG.info("Image Classifier Model taking too long to respond!");
            }
            // return classification result
            return result;
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
