package com.github.imdmk.automessage.bukkit;

import com.github.imdmk.automessage.AutoMessage;
import com.github.imdmk.automessage.bukkit.listener.MessageTriggerListener;
import com.github.imdmk.automessage.bukkit.placeholder.ExternalPlaceholderResolverFactory;
import com.github.imdmk.automessage.command.CommandRegistrar;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public final class AutoMessagePlugin {

    private final AutoMessage automessage;
    private final AudienceProvider audiences;
    private final MessageTriggerListener triggerListener;
    private final LiteCommands<?> liteCommands;
    private final MetricsService metricsService;

    public AutoMessagePlugin(Plugin plugin, String platformName, TaskScheduler scheduler) {
        this(plugin, platformName, scheduler, builder -> { });
    }

    // commands is where a fork of Bukkit adds what only it needs. Folia is the one that does:
    // LiteCommands runs commands through Bukkit's global scheduler, which Folia does not have, and
    // says so at startup unless its own extension is installed. That extension is Folia's
    // dependency, not this module's, so it is handed in rather than reached for.
    public AutoMessagePlugin(
            Plugin plugin,
            String platformName,
            TaskScheduler scheduler,
            Consumer<LiteCommandsBuilder<CommandSender, ?, ?>> commands
    ) {
        final Server server = plugin.getServer();
        final PluginLogger logger = new BukkitPluginLogger(plugin.getLogger());

        this.audiences = BukkitAudiences.create(plugin);

        final BukkitPlatform platform =
                new BukkitPlatform(platformName, server, audiences, scheduler);

        this.automessage = new AutoMessage(
                platform,
                logger,
                plugin.getDataFolder(),
                ExternalPlaceholderResolverFactory.create(server, logger)
        );

        final BukkitViewerFactory viewers = new BukkitViewerFactory(audiences);

        this.triggerListener = new MessageTriggerListener(viewers, automessage.triggerService());
        server.getPluginManager().registerEvents(triggerListener, plugin);

        final var commandBuilder = LiteBukkitFactory.builder(AutoMessage.NAME, plugin, server);
        CommandRegistrar.configure(commandBuilder, automessage, viewers);
        commands.accept(commandBuilder);

        this.liteCommands = commandBuilder.build();

        this.metricsService = new MetricsService(plugin);
    }

    public void disable() {
        // Bukkit clears a plugin's handlers when it is disabled, but this class is also torn down
        // by the loader on its own, so the listener is detached explicitly.
        HandlerList.unregisterAll(triggerListener);

        automessage.shutdown();

        liteCommands.unregister();
        audiences.close();
        metricsService.shutdown();
    }

}
