package com.github.imdmk.automessage.folia;

import com.github.imdmk.automessage.bukkit.AutoMessagePlugin;
import dev.rollczi.litecommands.folia.FoliaExtension;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AutoMessageFoliaPlugin extends JavaPlugin {

    private AutoMessagePlugin plugin;

    @Override
    public void onEnable() {
        this.plugin = StaleFoliaWarning.silenced(getLogger(), () -> new AutoMessagePlugin(
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
                // See StaleFoliaWarning for why its startup warning is dropped rather than fixed.
                builder -> builder.extension(new FoliaExtension(this))
        ));
    }

    @Override
    public void onDisable() {
        if (this.plugin != null) {
            this.plugin.disable();
            this.plugin = null;
        }
    }
}
