package com.hanami.demoplugin;

import com.hanami.api.Configuration;
import com.hanami.api.Hanami;
import com.hanami.plugin.ExtentionPlugin;

public class PluginOne implements ExtentionPlugin {

    @Override
    public Hanami inject(Hanami hanami) {
        hanami.register(new Configuration("demo-plugin"));
        return hanami;
    }
}
