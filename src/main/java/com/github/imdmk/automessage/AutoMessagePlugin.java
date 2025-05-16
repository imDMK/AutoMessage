package com.github.imdmk.automessage;

import org.bukkit.plugin.java.JavaPlugin;

public class AutoMessagePlugin extends JavaPlugin {

    /** bStats Metrics service ID for reporting plugin statistics */
    public static final int METRICS_SERVICE_ID = 19487;

    private AutoMessage autoMessage;

    @Override
    public void onEnable() {
        this.autoMessage = new AutoMessage(this);
    }

    @Override
    public void onDisable() {
        this.autoMessage.disable();
    }
}
