package com.github.imdmk.automessage;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.command.dispatcher.DisableCommand;
import com.github.imdmk.automessage.command.dispatcher.EnableCommand;
import com.github.imdmk.automessage.command.reload.ReloadCommand;
import com.github.imdmk.automessage.command.view.ViewCommand;
import com.github.imdmk.automessage.config.ConfigManager;
import com.github.imdmk.automessage.config.ConfigReloadService;
import com.github.imdmk.automessage.message.MessageConfig;
import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.platform.litecommands.argument.ScheduledMessageArgument;
import com.github.imdmk.automessage.platform.litecommands.argument.UnknownScheduledMessage;
import com.github.imdmk.automessage.platform.litecommands.handler.InvalidUsageHandlerImpl;
import com.github.imdmk.automessage.platform.litecommands.handler.MissingPermissionsHandlerImpl;
import com.github.imdmk.automessage.platform.litecommands.handler.NoticeResultHandlerImpl;
import com.github.imdmk.automessage.platform.litecommands.handler.UnknownScheduledMessageHandler;
import com.github.imdmk.automessage.platform.logger.BukkitPluginLogger;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.platform.metrics.MetricsService;
import com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolver;
import com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolverFactory;
import com.github.imdmk.automessage.platform.scheduler.BukkitTaskScheduler;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.ScheduledMessageRepository;
import com.github.imdmk.automessage.scheduled.ScheduledMessageSender;
import com.github.imdmk.automessage.scheduled.ScheduledMessagesConfig;
import com.github.imdmk.automessage.scheduled.audience.filter.AudienceFilter;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcher;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcherConfig;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcherService;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import com.github.imdmk.automessage.scheduled.dispatcher.ScheduledMessageDispatcherFactory;
import com.github.imdmk.automessage.scheduled.selector.MessageSelectorProvider;
import com.github.imdmk.automessage.scheduled.selector.MessageSelectorType;
import com.github.imdmk.automessage.scheduled.trigger.MessageTriggerListener;
import com.github.imdmk.automessage.scheduled.trigger.MessageTriggerService;
import com.github.imdmk.automessage.scheduled.trigger.PlayerCountMilestones;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

final class AutoMessagePlugin {

    private static final String PLUGIN_PREFIX = "AutoMessage";
    private static final String DONATE_URL = "https://paypal.me/dominiksuliga";

    private final PluginLogger logger;
    private final ConfigManager configManager;

    private final MessageService messageService;
    private final TaskScheduler taskScheduler;
    private final MessageDispatcherService dispatcherService;
    private final LiteCommands<?> liteCommands;
    private final MetricsService metricsService;
    private final MessageTriggerListener triggerListener;

    AutoMessagePlugin(Plugin plugin) {
        final Server server = plugin.getServer();
        this.logger = new BukkitPluginLogger(plugin.getLogger());

        this.configManager = new ConfigManager(logger, plugin.getDataFolder());

        final MessageConfig messageConfig = configManager.create(MessageConfig.class);
        final ScheduledMessagesConfig scheduledMessagesConfig = configManager.create(ScheduledMessagesConfig.class);
        final MessageDispatcherConfig dispatcherConfig = configManager.create(MessageDispatcherConfig.class);

        final ConfigReloadService configReloadService = new ConfigReloadService(configManager);

        this.messageService = new MessageService(messageConfig, plugin);
        this.taskScheduler = new BukkitTaskScheduler(plugin, server.getScheduler());

        final ScheduledMessageRepository messageRepository = ScheduledMessageRepository.config(scheduledMessagesConfig);
        final ExternalPlaceholderResolver placeholderResolver =
                ExternalPlaceholderResolverFactory.create(server, logger);

        final ScheduledMessageSender messageSender =
                new ScheduledMessageSender(server, messageService, placeholderResolver);

        final ScheduledMessageDispatcherFactory dispatcherFactory =
                selector -> new MessageDispatcher(messageSender, selector, AudienceFilter.ruleFilter());

        this.dispatcherService = new MessageDispatcherService(
                logger,
                server,
                taskScheduler,
                dispatcherConfig,
                messageRepository,
                dispatcherFactory
        );

        dispatcherService.start();
        configReloadService.register(dispatcherService);

        // A trigger dispatches the one message its event names, so this dispatcher's selector is
        // never consulted. It still gets a real one rather than a trap that would only fire if
        // some later change started rotating through triggered messages.
        final MessageDispatcher triggerDispatcher = dispatcherFactory.create(
                new MessageSelectorProvider(() -> MessageSelectorType.SEQUENTIAL)
        );

        this.triggerListener = new MessageTriggerListener(new MessageTriggerService(
                server,
                taskScheduler,
                messageRepository,
                triggerDispatcher,
                AudienceFilter.ruleFilter(),
                new PlayerCountMilestones()
        ));

        server.getPluginManager().registerEvents(triggerListener, plugin);

        this.liteCommands = LiteBukkitFactory.builder(PLUGIN_PREFIX, plugin, server)
                .invalidUsage(new InvalidUsageHandlerImpl(messageService))
                .missingPermission(new MissingPermissionsHandlerImpl(messageService))
                .result(Notice.class, new NoticeResultHandlerImpl(messageService))
                .result(UnknownScheduledMessage.class, new UnknownScheduledMessageHandler(messageService))

                .argument(ScheduledMessage.class, new ScheduledMessageArgument(messageRepository))

                .commands(
                        new DisableCommand(dispatcherConfig, messageService),
                        new EnableCommand(dispatcherConfig, messageService),
                        new ReloadCommand(logger, configReloadService, taskScheduler, messageService),
                        new ViewCommand(messageSender, messageService)
                )

                .build();

        this.metricsService = new MetricsService(plugin);

        logger.info("%s plugin enabled.", PLUGIN_PREFIX);
        logger.info("Enjoying %s? You can support its development at %s - thank you!", PLUGIN_PREFIX, DONATE_URL);
    }

    void disable() {
        // Bukkit clears a plugin's handlers when it is disabled, but this class is also torn down
        // by the loader on its own, so the listener is detached explicitly.
        HandlerList.unregisterAll(triggerListener);

        dispatcherService.stop();
        configManager.saveAll();
        configManager.clearAll();
        messageService.shutdown();
        taskScheduler.shutdown();
        liteCommands.unregister();
        metricsService.shutdown();

        logger.info("%s plugin disabled successfully.", PLUGIN_PREFIX);
    }
}
