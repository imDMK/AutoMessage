package com.github.imdmk.automessage;

import org.bukkit.plugin.java.JavaPlugin;

public final class AutoMessagePluginLoader extends JavaPlugin {

    private AutoMessagePlugin plugin;

    @Override
    public void onEnable() {
        this.plugin = new AutoMessagePlugin(this);
    }

    @Override
    public void onDisable() {
        if (this.plugin != null) {
            this.plugin.disable();
            this.plugin = null;
        }
    }
}
