package com.github.imdmk.automessage.folia;

import com.github.imdmk.automessage.bukkit.AutoMessagePlugin;
import dev.rollczi.litecommands.folia.FoliaExtension;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AutoMessageFoliaPlugin extends JavaPlugin {

    private AutoMessagePlugin plugin;

    @Override
    public void onEnable() {
        this.plugin = new AutoMessagePlugin(
                this,
                "Folia",
                new FoliaTaskScheduler(
                        this,
                        Bukkit.getGlobalRegionScheduler(),
                        Bukkit.getAsyncScheduler()
                ),
                // Without this, a command run from a region thread reaches Bukkit's global
                // scheduler, which Folia removed. The console never hits it - LiteCommands runs
                // inline when it is already on the primary thread - but a player would.
                //
                // The startup warning about this extension is emitted by BukkitScheduler's own
                // constructor, which the factory builds before any extension can be added, so it
                // is stale by the time it is printed rather than a sign this did not take.
                builder -> builder.extension(new FoliaExtension(this))
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
