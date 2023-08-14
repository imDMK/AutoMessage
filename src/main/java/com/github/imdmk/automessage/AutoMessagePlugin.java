package com.github.imdmk.automessage;

import org.bukkit.plugin.java.JavaPlugin;

public class AutoMessagePlugin extends JavaPlugin {

    private AutoMessage autoMessage;

    @Override
    public void onEnable() {
        this.autoMessage = new AutoMessage(this);
    }

    @Override
    public void onDisable() {
        this.autoMessage.onDisable();
    }
}
