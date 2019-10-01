package com.evergarden.cms.context.installer;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class CreateFileStep implements Step<CreateFileStep> {


    private void create(){
        try {
            FileUtils.forceMkdir(new File("./plugins"));
            FileUtils.forceMkdir(new File("./template/admin"));
            FileUtils.forceMkdir(new File("./template/theme"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute() {
        create();
    }

    @Override
    public boolean isOk() {
        return false;
    }

    @Override
    public CreateFileStep getInstance() {
        return null;
    }
}
