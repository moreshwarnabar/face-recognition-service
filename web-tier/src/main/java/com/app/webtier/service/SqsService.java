package com.app.webtier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SqsService implements ISqsService {

    private static final Logger LOG = LoggerFactory.getLogger(SqsService.class);

    private static final String REQUEST_QUEUE = "https://sqs.us-east-1.amazonaws.com/992382716564/1229975385-req-queue";

    private static final String RESULT_QUEUE = "https://sqs.us-east-1.amazonaws.com/992382716564/1229975385-resp-queue";

    private final SqsClient sqsClient;

    public SqsService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @Override
    public boolean sendMessage(String body) {
        boolean isSent = false;
        try {
            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(REQUEST_QUEUE)
                    .messageBody(body)
                    .build();
            sqsClient.sendMessage(request);
            LOG.info("Message sent with body: {}", body);
            isSent = true;
        } catch (SqsException e) {
            LOG.error(e.getMessage());
        }
        return isSent;
    }

    @Override
    public List<Message> receiveMessage() {
        List<Message> message = null;
        try {
            ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                    .queueUrl(RESULT_QUEUE)
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(10)
                    .build();
            message = sqsClient.receiveMessage(request).messages();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return message;
    }

    @Override
    public boolean deleteMessage(List<Message> messages) {
        boolean isDeleted = false;
        try {
            List<DeleteMessageBatchRequestEntry> entries = new ArrayList<>();
            for (int i = 0; i < messages.size(); i++)
                entries.add(DeleteMessageBatchRequestEntry.builder()
                                .id(String.valueOf(i))
                                .receiptHandle(messages.get(i).receiptHandle())
                                .build());
            DeleteMessageBatchRequest request = DeleteMessageBatchRequest.builder()
                    .queueUrl(RESULT_QUEUE)
                    .entries(entries)
                    .build();
            sqsClient.deleteMessageBatch(request);
            LOG.info("Deleted messages!");
            isDeleted = true;
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return isDeleted;
    }

    @Override
    public int getApproxNumofMessagesInQueue() {
        int numOfMessages = -1;
        try {
            // create list of attributes to query
            List<QueueAttributeName> attrNames = new ArrayList<>();
            attrNames.add(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES);
            // create a request to retrieve the queue attributes
            GetQueueAttributesRequest request = GetQueueAttributesRequest.builder()
                    .queueUrl(REQUEST_QUEUE)
                    .attributeNames(attrNames)
                    .build();
            GetQueueAttributesResponse response = sqsClient.getQueueAttributes(request);
            Map<QueueAttributeName, String> attributes = response.attributes();
            // extract the number of messages if available
            if (attributes == null || !attributes.containsKey(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES))
                return numOfMessages;
            numOfMessages = Integer.parseInt(attributes.get(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES));
            LOG.info("Approximate number of messages: {}", numOfMessages);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return numOfMessages;
    }

}
