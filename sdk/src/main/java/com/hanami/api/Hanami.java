package com.hanami.api;

import java.util.ArrayList;

public class Hanami {

    private ArrayList<Configuration> configurations;

    public void register(Configuration configuration){
        configurations.add(configuration);
    }
}
