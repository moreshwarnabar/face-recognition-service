package com.app.webtier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateTagsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.InstanceStateChange;
import software.amazon.awssdk.services.ec2.model.InstanceType;
import software.amazon.awssdk.services.ec2.model.RunInstancesRequest;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;
import software.amazon.awssdk.services.ec2.model.ShutdownBehavior;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesRequest;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class Ec2Service implements IEc2Service {

    private static final Logger LOG = LoggerFactory.getLogger(Ec2Service.class);

    private static final String AMI_ID = "ami-0cc01baf101e1961b";

    private static final String KEY = "cse546-part1";

    private final Ec2Client ec2Client;

    public Ec2Service(Ec2Client ec2Client) {
        this.ec2Client = ec2Client;
    }

    @Override
    public String startEc2Instance(String name) {
        String instanceId = "";
        try {
            // create a run instance request
            RunInstancesRequest request = RunInstancesRequest.builder()
                    .imageId(AMI_ID)
                    .instanceType(InstanceType.T2_MICRO)
                    .keyName(KEY)
                    .minCount(1)
                    .maxCount(1)
                    .instanceInitiatedShutdownBehavior(ShutdownBehavior.TERMINATE)
                    .build();

            RunInstancesResponse response = ec2Client.runInstances(request);
            instanceId = response.instances().get(0).instanceId();
            // create tags for the ec2 instance
            Tag tag = Tag.builder()
                    .key("Name")
                    .value(name)
                    .build();
            CreateTagsRequest tagsRequest = CreateTagsRequest.builder()
                    .resources(instanceId)
                    .tags(tag)
                    .build();
            // attach the tags to the EC2 instance
            ec2Client.createTags(tagsRequest);
            LOG.info("started an instance with name: {}", name);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return instanceId;
    }

    @Override
    public void terminateEc2Instance(String instanceId) {
        try {
            TerminateInstancesRequest request = TerminateInstancesRequest.builder()
                    .instanceIds(instanceId)
                    .build();
            TerminateInstancesResponse response = ec2Client.terminateInstances(request);
            List<InstanceStateChange> terminated = response.terminatingInstances();
            for (InstanceStateChange s : terminated) {
                LOG.info("Terminated an instance with id: {}", s.instanceId());
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    @Override
    public int numberOfRunningInstances() {
        int numOfRunningInstances = 0;
        try {
            // create filter to only query running EC2 instances
            Filter filter = Filter.builder()
                    .name("instance-state-name")
                    .values("running", "pending")
                    .build();
            List<Filter> filters = new ArrayList<>();
            filters.add(filter);
            // create a request to describe the EC2 instances
            DescribeInstancesRequest request = DescribeInstancesRequest.builder()
                    .filters(filters)
                    .maxResults(25)
                    .build();
            // retrieve the number of running EC2 instances
            List<Instance> instances = ec2Client.describeInstancesPaginator(request).stream()
                    .flatMap(response -> response.reservations().stream())
                    .flatMap(reservation -> reservation.instances().stream())
                    .toList();
            numOfRunningInstances = instances.size();
            LOG.info("Number of running instances: {}", numOfRunningInstances);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return numOfRunningInstances;
    }

}
