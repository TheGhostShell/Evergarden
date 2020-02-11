package com.evergarden.cms;

import org.testcontainers.containers.GenericContainer;

import java.io.IOException;

public class MongoDbContainer extends GenericContainer<MongoDbContainer> {
    public MongoDbContainer(String tagImage) {
        super(tagImage);
    }

    @Override
    public void close() {
        dockerClient.stopContainerCmd("docker stop " + this.getContainerId());
        dockerClient.killContainerCmd("docker kill " + this.getContainerId());
        dockerClient.removeContainerCmd("docker rm " + this.getContainerId());
        super.close();
    }
}
