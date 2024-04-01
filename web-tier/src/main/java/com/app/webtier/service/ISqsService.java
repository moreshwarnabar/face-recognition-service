package com.app.webtier.service;

import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

public interface ISqsService {

    boolean sendMessage(String body);

    List<Message> receiveMessage();

    boolean deleteMessage(List<Message> messages);

    int getApproxNumofMessagesInQueue();

}
