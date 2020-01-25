package com.evergarden.cms;

import org.testcontainers.containers.GenericContainer;

public class MongoDbContainer extends GenericContainer<MongoDbContainer> {
    public MongoDbContainer(String tagImage) {
        super(tagImage);
    }
}
