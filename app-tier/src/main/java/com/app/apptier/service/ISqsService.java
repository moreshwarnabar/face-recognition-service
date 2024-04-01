package com.app.apptier.service;


import software.amazon.awssdk.services.sqs.model.Message;

public interface ISqsService {

    Message receiveMessage();

    void sendMessage(String body);

    void deleteMessage(Message message);

}
