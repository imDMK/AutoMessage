package com.github.imdmk.automessage;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.command.dispatcher.DisableCommand;
import com.github.imdmk.automessage.command.dispatcher.EnableCommand;
import com.github.imdmk.automessage.command.reload.ReloadCommand;
import com.github.imdmk.automessage.config.ConfigManager;
import com.github.imdmk.automessage.message.MessageConfig;
import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.platform.litecommands.handler.InvalidUsageHandlerImpl;
import com.github.imdmk.automessage.platform.litecommands.handler.MissingPermissionsHandlerImpl;
import com.github.imdmk.automessage.platform.litecommands.handler.NoticeResultHandlerImpl;
import com.github.imdmk.automessage.platform.logger.BukkitPluginLogger;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.platform.metrics.MetricsService;
import com.github.imdmk.automessage.platform.scheduler.BukkitTaskScheduler;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import com.github.imdmk.automessage.scheduled.ScheduledMessagesConfig;
import com.github.imdmk.automessage.scheduled.audience.filter.AudienceFilter;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcher;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcherConfig;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcherTask;
import com.github.imdmk.automessage.scheduled.selector.MessageSelectorFactory;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

final class AutoMessagePlugin {

    private static final String PLUGIN_PREFIX = "AutoMessage";

    private final PluginLogger logger;
    private final ConfigManager configManager;

    private final MessageService messageService;
    private final TaskScheduler taskScheduler;
    private final LiteCommands<?> liteCommands;
    private final MetricsService metricsService;

    AutoMessagePlugin(Plugin plugin) {
        final Server server = plugin.getServer();
        this.logger = new BukkitPluginLogger(plugin.getLogger());

        this.configManager = new ConfigManager(logger, plugin.getDataFolder());

        final MessageConfig messageConfig = configManager.create(MessageConfig.class);
        final ScheduledMessagesConfig scheduledMessagesConfig = configManager.create(ScheduledMessagesConfig.class);
        final MessageDispatcherConfig dispatcherConfig = configManager.create(MessageDispatcherConfig.class);

        this.messageService = new MessageService(messageConfig, plugin);
        this.taskScheduler = new BukkitTaskScheduler(plugin, server.getScheduler());

        final MessageDispatcher messageDispatcher = new MessageDispatcher(
                messageService,
                MessageSelectorFactory.create(dispatcherConfig.selector),
                AudienceFilter.ruleFilter(),
                () -> scheduledMessagesConfig.messages
        );

        final MessageDispatcherTask dispatcherTask = new MessageDispatcherTask(server, dispatcherConfig, messageDispatcher);
        taskScheduler.runTimerAsync(dispatcherTask);

        this.liteCommands = LiteBukkitFactory.builder(PLUGIN_PREFIX, plugin, server)
                .invalidUsage(new InvalidUsageHandlerImpl(messageService))
                .missingPermission(new MissingPermissionsHandlerImpl(messageService))
                .result(Notice.class, new NoticeResultHandlerImpl(messageService))

                .commands(
                        new DisableCommand(dispatcherConfig, messageService),
                        new EnableCommand(dispatcherConfig, messageService),
                        new ReloadCommand(logger, configManager, taskScheduler, messageService)
                )

                .build();

        this.metricsService = new MetricsService(plugin);

        logger.info("%s plugin enabled.", PLUGIN_PREFIX);
    }

    void disable() {
        configManager.saveAll();
        configManager.clearAll();
        messageService.shutdown();
        taskScheduler.shutdown();
        liteCommands.unregister();
        metricsService.shutdown();

        logger.info("%s plugin disabled successfully.", PLUGIN_PREFIX);
    }
}
