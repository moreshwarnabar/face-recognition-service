package com.app.webtier.controller;

import com.app.webtier.service.IEc2Service;
import com.app.webtier.service.SqsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScaleController {

    private static final Logger LOG = LoggerFactory.getLogger(ScaleController.class);

    private static final String INSTANCE_NAME = "app-tier-instance-%s";

    private final IEc2Service ec2Service;

    private final SqsService sqsService;

    public ScaleController(IEc2Service ec2Service, SqsService sqsService) {
        this.ec2Service = ec2Service;
        this.sqsService = sqsService;
    }

    @Scheduled(fixedDelay = 2000)
    public void scaleAppTierInstances() {
        try {
            // fetch number of messages in the request queue
            int numOfMessages = sqsService.getApproxNumofMessagesInQueue();
            if (numOfMessages != -1) {
                // fetch number of EC2 instances currently running
                int numOfInstances = ec2Service.numberOfRunningInstances();
                if (numOfMessages > numOfInstances - 1) {
                    // messages are arriving in queue faster than they are being processed
                    startAppTierInstances(numOfMessages, numOfInstances);
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    private void startAppTierInstances(int numOfMessages, int numOfInstances) {
        for (int i = numOfInstances; i <= (numOfMessages - numOfInstances) + 1 && i <= 20; i++) {
            String name = String.format(INSTANCE_NAME, i);
            String instanceId = ec2Service.startEc2Instance(name);
            LOG.info("Created app tier instance: {}", instanceId);
        }
    }

}
