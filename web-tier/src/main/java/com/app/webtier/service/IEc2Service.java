package com.app.webtier.service;

public interface IEc2Service {

    // start an instance
    String startEc2Instance(String name);

    // terminate an instance
    void terminateEc2Instance(String instanceId);

    // number of running instances
    int numberOfRunningInstances();

}
