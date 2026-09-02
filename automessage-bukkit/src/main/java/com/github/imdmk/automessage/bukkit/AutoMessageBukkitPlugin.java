package com.github.imdmk.automessage.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public final class AutoMessageBukkitPlugin extends JavaPlugin {

    private AutoMessagePlugin plugin;

    @Override
    public void onEnable() {
        this.plugin = new AutoMessagePlugin(
                this,
                "Bukkit",
                new BukkitTaskScheduler(this, getServer().getScheduler())
        );
    }

    @Override
    public void onDisable() {
        if (this.plugin != null) {
            this.plugin.disable();
            this.plugin = null;
        }
    }
}
