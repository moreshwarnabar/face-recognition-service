package com.app.apptier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

import java.util.List;

@Service
public class SqsService implements ISqsService {

    private static final Logger LOG = LoggerFactory.getLogger(SqsService.class);

    private static final String REQUEST_QUEUE = "https://sqs.us-east-1.amazonaws.com/992382716564/1229975385-req-queue";

    private static final String RESULT_QUEUE = "https://sqs.us-east-1.amazonaws.com/992382716564/1229975385-resp-queue";

    private final SqsClient sqsClient;

    SqsService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @Override
    public Message receiveMessage() {
        try {
            ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                    .queueUrl(REQUEST_QUEUE)
                    .maxNumberOfMessages(1)
                    .waitTimeSeconds(3)
                    .build();

            List<Message> messages = sqsClient.receiveMessage(request).messages();
            if (messages == null || messages.isEmpty())
                return null;
            Message message = messages.get(0);
            LOG.info("Received message: {}", message.body());
            return message;
        } catch (SqsException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(String body) {
        try {
            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(RESULT_QUEUE)
                    .messageBody(body)
                    .build();

            sqsClient.sendMessage(request);
            LOG.info("Sent message with body: {}", body);
        } catch (SqsException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteMessage(Message message) {
        try {
            DeleteMessageRequest request = DeleteMessageRequest.builder()
                    .queueUrl(REQUEST_QUEUE)
                    .receiptHandle(message.receiptHandle())
                    .build();

            sqsClient.deleteMessage(request);
            LOG.info("Deleted message: {}", message);
        } catch (SqsException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
