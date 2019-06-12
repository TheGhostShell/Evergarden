package com.hanami.sdk.api;

import com.google.common.eventbus.EventBus;

import java.util.ArrayList;

public class Hanami {
    
    private EventBus eventBus;

    private ArrayList<Configuration> configurations= new ArrayList<>();
    
    public Hanami setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        return this;
    }
    
    public void register(Configuration configuration){
        configurations.add(configuration);
    }
    
//    public void addRoute(Route route) {
//
//    }
    
    public void addListener(Object object){
        eventBus.register(object);
    }
    
    public void emit(Object object) {
        eventBus.post(object);
    }
}
