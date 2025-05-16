package com.github.imdmk.automessage;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.configuration.ConfigurationManager;
import com.github.imdmk.automessage.configuration.implementation.PluginConfiguration;
import com.github.imdmk.automessage.feature.command.builder.configuration.CommandConfiguration;
import com.github.imdmk.automessage.feature.command.builder.configuration.CommandConfigurator;
import com.github.imdmk.automessage.feature.command.builder.handler.MissingPermissionHandler;
import com.github.imdmk.automessage.feature.command.builder.handler.UsageHandler;
import com.github.imdmk.automessage.feature.command.builder.player.PlayerArgument;
import com.github.imdmk.automessage.feature.command.builder.player.PlayerContextual;
import com.github.imdmk.automessage.feature.command.implementation.DelayCommand;
import com.github.imdmk.automessage.feature.command.implementation.ReloadCommand;
import com.github.imdmk.automessage.feature.message.MessageConfiguration;
import com.github.imdmk.automessage.feature.message.MessageResultHandler;
import com.github.imdmk.automessage.feature.message.MessageService;
import com.github.imdmk.automessage.feature.message.auto.AutoMessageConfiguration;
import com.github.imdmk.automessage.feature.message.auto.dispatcher.AutoMessageDispatcher;
import com.github.imdmk.automessage.feature.update.UpdateController;
import com.github.imdmk.automessage.feature.update.UpdateService;
import com.github.imdmk.automessage.scheduler.BukkitTaskScheduler;
import com.github.imdmk.automessage.scheduler.TaskScheduler;
import com.google.common.base.Stopwatch;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Core plugin initializer responsible for setting up configuration,
 * services, commands, schedulers, and external integrations.
 */
class AutoMessage {

    private final Server server;
    private final Logger logger;

    private final ConfigurationManager configurationManager;

    private final MessageService messageService;

    private final TaskScheduler taskScheduler;

    private final LiteCommands<CommandSender> liteCommands;

    private final Metrics metrics;

    AutoMessage(Plugin plugin) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        this.server = plugin.getServer();
        this.logger = plugin.getLogger();

        /* Configuration */
        this.configurationManager = new ConfigurationManager(this.logger, plugin.getDataFolder());

        PluginConfiguration pluginConfiguration = this.configurationManager.create(PluginConfiguration.class);
        MessageConfiguration messageConfiguration = this.configurationManager.create(MessageConfiguration.class);
        AutoMessageConfiguration autoMessageConfiguration = this.configurationManager.create(AutoMessageConfiguration.class);
        CommandConfiguration commandConfiguration = this.configurationManager.create(CommandConfiguration.class);

        /* Services */
        this.messageService = new MessageService(messageConfiguration, BukkitAudiences.create(plugin), MiniMessage.miniMessage());
        UpdateService updateService = new UpdateService(pluginConfiguration, plugin.getDescription());

        /* Scheduler */
        this.taskScheduler = new BukkitTaskScheduler(plugin, this.server);

        /* Dispatcher */
        AutoMessageDispatcher autoMessageDispatcher = new AutoMessageDispatcher(this.configurationManager, autoMessageConfiguration, this.messageService, this.taskScheduler);
        autoMessageDispatcher.schedule();

        /* Controllers */
        Stream.of(
                new UpdateController(this.logger, pluginConfiguration, this.messageService, updateService, this.taskScheduler)
        ).forEach(listener -> this.server.getPluginManager().registerEvents(listener, plugin));

        /* LiteCommands */
        this.liteCommands = LiteBukkitFactory.builder("AutoMessage", plugin, this.server)
                .argument(Player.class, new PlayerArgument(this.server, messageConfiguration))

                .context(Player.class, new PlayerContextual())
                .result(Notice.class, new MessageResultHandler(this.messageService))

                .missingPermission(new MissingPermissionHandler(this.messageService))
                .invalidUsage(new UsageHandler(this.messageService))

                .commands(
                        new DelayCommand(this.messageService, autoMessageDispatcher),
                        new ReloadCommand(this.logger, this.configurationManager, this.messageService)
                )

                .editorGlobal(new CommandConfigurator(this.logger, commandConfiguration))

                .build();

        /* Metrics */
        this.metrics = new Metrics(plugin, AutoMessagePlugin.METRICS_SERVICE_ID);

        this.logger.info("Enabled plugin in " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms.");
    }

    /**
     * Gracefully shuts down all plugin components and resources.
     * Called during plugin disable.
     */
    void disable() {
        this.configurationManager.shutdown();
        this.messageService.close();
        this.liteCommands.unregister();
        this.metrics.shutdown();
        this.taskScheduler.shutdown();

        this.logger.info("Successfully disabled plugin.");
    }
}
