package com.evergarden.cms.context.installer;


import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class DownloadThemeStep implements Step<DownloadThemeStep> {

    @Override
    public void execute() {
        try {
            File zip = new File("./template/theme/TraingTemplate.zip");
            FileUtils.copyURLToFile(
                    new URL("https://drive.google.com/uc?export=download&id=1TqdO2TglECiNxgZ-WM6kFXOcv0ZLFzBv"),
                    zip,
                    10000,
                    10000
            );
            ZipFile preZip = new ZipFile(zip);
            preZip.extractAll("./template/theme/");
        } catch (IOException e) {
            e.printStackTrace();
        }catch (ZipException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isOk() {
        return false;
    }

    @Override
    public DownloadThemeStep getInstance() {
        return null;
    }
}
