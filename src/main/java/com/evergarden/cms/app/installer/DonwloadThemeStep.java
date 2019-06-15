package com.evergarden.cms.app.installer;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class DonwloadThemeStep implements Step<DonwloadThemeStep> {

    @Override
    public void execute() {
        try {
            FileUtils.copyURLToFile(
                    new URL("https://drive.google.com/uc?export=download&id=1uZjDzwtGTyAKEVR3Kra72-aL3XtxJWY8"),
                    new File("./template/theme/Theme.rar"),
                    10000,
                    10000
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isOk() {
        return false;
    }

    @Override
    public DonwloadThemeStep getInstance() {
        return null;
    }
}
