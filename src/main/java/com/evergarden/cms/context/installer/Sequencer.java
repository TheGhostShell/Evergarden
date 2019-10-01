package com.evergarden.cms.context.installer;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class Sequencer {
    private ArrayList<Step> sequence = new ArrayList<>();

    public Sequencer addAfter(){return this;}
    public Sequencer addBefore(){return this;}
    public Sequencer add(Step step){
        sequence.add(step);
        return this;
    }

    private void build(){
        add(new CreateFileStep());
        // add(new DownloadThemeStep());
    }

    @Bean
    public Sequencer install(){
        build();
        System.out.println("Sequencer run");
        sequence
                .stream()
                .peek(Step::execute)
                .count();

        return this;
    }
}
