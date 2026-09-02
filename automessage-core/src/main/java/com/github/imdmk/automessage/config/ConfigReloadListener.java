package com.github.imdmk.automessage.config;

@FunctionalInterface
public interface ConfigReloadListener {

    void onConfigReload();
}
