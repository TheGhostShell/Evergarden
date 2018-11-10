package com.hanami.sdk.demoplugin;

import com.hanami.sdk.api.Configuration;
import com.hanami.sdk.api.Hanami;
import com.hanami.sdk.plugin.ExtentionPlugin;

public class PluginOne implements ExtentionPlugin {

    @Override
    public Hanami inject(Hanami hanami) {
        hanami.register(new Configuration("demo-plugin"));
        System.out.println("Plugin charged");
        return hanami;
    }
}
