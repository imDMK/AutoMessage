package com.github.imdmk.automessage;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.configuration.ConfigurationManager;
import com.github.imdmk.automessage.configuration.PluginConfig;
import com.github.imdmk.automessage.feature.command.builder.configuration.CommandConfig;
import com.github.imdmk.automessage.feature.command.builder.configuration.CommandEditor;
import com.github.imdmk.automessage.feature.command.builder.handler.MissingPermissionHandler;
import com.github.imdmk.automessage.feature.command.builder.handler.UsageHandler;
import com.github.imdmk.automessage.feature.command.builder.player.PlayerArgument;
import com.github.imdmk.automessage.feature.command.builder.player.PlayerContextual;
import com.github.imdmk.automessage.feature.command.implementation.DelayCommand;
import com.github.imdmk.automessage.feature.command.implementation.DisableCommand;
import com.github.imdmk.automessage.feature.command.implementation.DispatchCommand;
import com.github.imdmk.automessage.feature.command.implementation.EnableCommand;
import com.github.imdmk.automessage.feature.command.implementation.ReloadCommand;
import com.github.imdmk.automessage.feature.message.MessageConfig;
import com.github.imdmk.automessage.feature.message.MessageResultHandler;
import com.github.imdmk.automessage.feature.message.MessageService;
import com.github.imdmk.automessage.feature.message.auto.AutoMessageNotice;
import com.github.imdmk.automessage.feature.message.auto.AutoMessageNoticeArgument;
import com.github.imdmk.automessage.feature.message.auto.AutoMessageNoticeConfig;
import com.github.imdmk.automessage.feature.message.auto.dispatcher.AutoMessageDispatcher;
import com.github.imdmk.automessage.feature.message.auto.eligibility.AutoMessageEligibilityEvaluator;
import com.github.imdmk.automessage.feature.message.auto.eligibility.DefaultEligibilityEvaluator;
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
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
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

    AutoMessage(@NotNull Plugin plugin) {
        Objects.requireNonNull(plugin, "plugin cannot be null.");
        Stopwatch stopwatch = Stopwatch.createStarted();

        this.server = plugin.getServer();
        this.logger = plugin.getLogger();

        /* Configuration */
        this.configurationManager = new ConfigurationManager(this.logger, plugin.getDataFolder());

        PluginConfig pluginConfig = this.configurationManager.create(PluginConfig.class);
        MessageConfig messageConfig = this.configurationManager.create(MessageConfig.class);
        AutoMessageNoticeConfig autoMessageNoticeConfig = this.configurationManager.create(AutoMessageNoticeConfig.class);
        CommandConfig commandConfig = this.configurationManager.create(CommandConfig.class);

        /* Services */
        this.messageService = new MessageService(messageConfig, BukkitAudiences.create(plugin), MiniMessage.miniMessage());
        UpdateService updateService = new UpdateService(pluginConfig, plugin.getDescription());

        /* Scheduler */
        this.taskScheduler = new BukkitTaskScheduler(plugin, this.server);

        /* Dispatcher */
        AutoMessageEligibilityEvaluator eligibilityEvaluator = new DefaultEligibilityEvaluator();

        AutoMessageDispatcher autoMessageDispatcher = new AutoMessageDispatcher(this.server, this.configurationManager, autoMessageNoticeConfig, this.messageService, this.taskScheduler, eligibilityEvaluator);
        autoMessageDispatcher.schedule();

        /* Controllers */
        Stream.of(
                new UpdateController(this.logger, pluginConfig, this.messageService, updateService, this.taskScheduler)
        ).forEach(listener -> this.server.getPluginManager().registerEvents(listener, plugin));

        /* LiteCommands */
        this.liteCommands = LiteBukkitFactory.builder("AutoMessage", plugin, this.server)
                .argument(Player.class, new PlayerArgument(this.server, messageConfig))
                .argument(AutoMessageNotice.class, new AutoMessageNoticeArgument(messageConfig, autoMessageNoticeConfig))

                .context(Player.class, new PlayerContextual())
                .result(Notice.class, new MessageResultHandler(this.messageService))

                .missingPermission(new MissingPermissionHandler(this.messageService))
                .invalidUsage(new UsageHandler(this.messageService))

                .commands(
                        new DelayCommand(this.messageService, autoMessageDispatcher),
                        new DisableCommand(this.messageService, autoMessageDispatcher),
                        new DispatchCommand(this.server, this.messageService, autoMessageDispatcher, eligibilityEvaluator),
                        new EnableCommand(this.messageService, autoMessageDispatcher),
                        new ReloadCommand(this.logger, this.configurationManager, this.messageService)
                )

                .editorGlobal(new CommandEditor(this.logger, commandConfig))

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
