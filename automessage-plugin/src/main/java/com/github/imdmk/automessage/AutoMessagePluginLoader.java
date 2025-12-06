package com.github.imdmk.automessage;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class AutoMessagePluginLoader extends JavaPlugin {

    private volatile AutoMessagePlugin pluginCore;

    @Override
    public void onEnable() {
        final Plugin plugin = this;

        pluginCore = new AutoMessagePlugin(plugin);
        pluginCore.enable(new DefaultPluginSettings());
    }

    @Override
    public void onDisable() {
        if (pluginCore != null) {
            pluginCore.disable();
            pluginCore = null;
        }
    }
}
