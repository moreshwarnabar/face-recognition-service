package com.app.webtier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Methods to extract the classification results of images
 *
 * @author Moreshwar Nabar
 */
@Service
public class WebTierService implements IWebTierService {

    private static final Logger LOG = LoggerFactory.getLogger(WebTierService.class);

    private static final Integer maxRetries = 3;

    private static final Map<String, String> results = new HashMap<>();

    private final S3Service s3Service;

    private final SqsService sqsService;

    public WebTierService(S3Service s3Service, SqsService sqsService) {
        this.s3Service = s3Service;
        this.sqsService = sqsService;
    }

    /**
     * Process the classification of the requested image
     *
     * @param file The image to be classified
     * @return The classification result
     */
    @Override
    public String processImages(MultipartFile file) {
        // extract the file name
        String imageName = file.getOriginalFilename();
        assert imageName != null;
        // remove the file extension from the name
        imageName = imageName.substring(0, imageName.length() - 4);
        LOG.info("Received image: {}", imageName);
        // save the image to the s3 bucket
        saveImage(imageName, file);
        // send the classification request to sqs
        sendMessage(imageName);
        // receive the classification response from sqs
        return handleResponse(imageName);
    }

    private void saveImage(String key, MultipartFile file) {
        int retryCount = 0;
        while (retryCount < maxRetries) {
            boolean result = s3Service.saveImage(key, file);
            if (result)
                return;
            retryCount++;
        }
    }

    private void sendMessage(String key) {
        int retryCount = 0;
        while (retryCount < maxRetries) {
            boolean result = sqsService.sendMessage(key);
            if (result)
                return;
            retryCount++;
        }
    }

    private String handleResponse(String imageName) {
        List<Message> messages = null;
        String response = results.getOrDefault(imageName, null);
        // receive the messages from response queue
        while (response == null) {
            messages = sqsService.receiveMessage();
            if (messages != null && !messages.isEmpty()) {
                // tabulate the received results and delete the message
                for (Message m : messages) {
                    String value = m.body();
                    String key = m.body().split(":")[0];
                    results.put(key, value);
                }
                sqsService.deleteMessage(messages);
            }
            response = results.getOrDefault(imageName, null);
        }
        results.remove(imageName);

        return response;
    }

}
