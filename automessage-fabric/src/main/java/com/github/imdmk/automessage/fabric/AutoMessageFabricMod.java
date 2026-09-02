package com.github.imdmk.automessage.fabric;

import com.github.imdmk.automessage.AutoMessage;
import com.github.imdmk.automessage.command.CommandRegistrar;
import com.github.imdmk.automessage.logging.Slf4jPluginLogger;
import com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolver;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.fabric.LiteFabricFactory;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import org.slf4j.LoggerFactory;

// Server-side on purpose: this broadcasts to the people connected to a server, which a client has
// none of, and a dedicated-server initializer means a client that installs it does nothing rather
// than half of something.
public final class AutoMessageFabricMod implements DedicatedServerModInitializer {

    private static final String MOD_ID = "automessage";

    private final FabricTaskScheduler scheduler = new FabricTaskScheduler();
    private final FabricServerHolder holder = new FabricServerHolder();

    private AutoMessage automessage;
    private LiteCommands<ServerCommandSource> liteCommands;

    // Everything is built here rather than once the server is up, because LiteCommands hooks
    // Fabric's command registration callback the moment it is built - and that callback has
    // already fired by the time SERVER_STARTED arrives, so commands registered then are simply
    // not there. Nothing here touches a MinecraftServer; what needs one goes through the holder.
    @Override
    public void onInitializeServer() {
        final FabricViewerFactory viewers = new FabricViewerFactory(holder);

        this.automessage = new AutoMessage(
                new FabricPlatform(scheduler, new FabricViewerRegistry(holder)),
                new Slf4jPluginLogger(LoggerFactory.getLogger(AutoMessage.NAME)),
                FabricLoader.getInstance().getConfigDir().resolve(MOD_ID).toFile(),
                // PlaceholderAPI is a Bukkit plugin; nothing here owns another mod's %tokens%.
                ExternalPlaceholderResolver.disabled()
        );

        final var commandBuilder = LiteFabricFactory.server();
        CommandRegistrar.configure(commandBuilder, automessage, viewers);
        this.liteCommands = commandBuilder.build();

        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> shutdown());

        // The scheduler has no clock of its own; this is it.
        ServerTickEvents.END_SERVER_TICK.register(server -> scheduler.tick());
    }

    private void onServerStarted(MinecraftServer server) {
        holder.started(server);

        new FabricTriggerListener(server, new FabricViewerFactory(holder), automessage.triggerService())
                .register();
    }

    private void shutdown() {
        if (automessage == null) {
            return;
        }

        automessage.shutdown();
        liteCommands.unregister();
        holder.stopped();

        this.automessage = null;
    }
}
