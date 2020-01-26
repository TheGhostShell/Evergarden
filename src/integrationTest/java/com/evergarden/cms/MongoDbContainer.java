package com.evergarden.cms;

import org.testcontainers.containers.GenericContainer;

import java.io.IOException;

public class MongoDbContainer extends GenericContainer<MongoDbContainer> {
    public MongoDbContainer(String tagImage) {
        super(tagImage);
    }

    @Override
    public void close() {
        super.close();
        dockerClient.stopContainerCmd("docker stop $(docker container ls -aq)");
        dockerClient.killContainerCmd("docker stop $(docker container ls -aq)");
        try {
            dockerClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
