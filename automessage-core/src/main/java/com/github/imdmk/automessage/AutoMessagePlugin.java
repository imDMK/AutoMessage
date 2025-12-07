package com.github.imdmk.automessage;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.command.dispatcher.DisableCommand;
import com.github.imdmk.automessage.command.dispatcher.EnableCommand;
import com.github.imdmk.automessage.command.reload.ReloadCommand;
import com.github.imdmk.automessage.config.ConfigManager;
import com.github.imdmk.automessage.platform.litecommands.handler.InvalidUsageHandlerImpl;
import com.github.imdmk.automessage.platform.litecommands.handler.MissingPermissionsHandlerImpl;
import com.github.imdmk.automessage.platform.litecommands.handler.NoticeResultHandlerImpl;
import com.github.imdmk.automessage.platform.logger.BukkitPluginLogger;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.platform.scheduler.BukkitTaskScheduler;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import com.github.imdmk.automessage.scheduled.ScheduledMessagesConfig;
import com.github.imdmk.automessage.scheduled.audience.filter.AudienceFilter;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcher;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcherConfig;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcherTask;
import com.github.imdmk.automessage.scheduled.selector.MessageSelector;
import com.github.imdmk.automessage.scheduled.selector.MessageSelectorFactory;
import com.github.imdmk.automessage.shared.message.MessageConfig;
import com.github.imdmk.automessage.shared.message.MessageService;
import com.github.imdmk.automessage.shared.validate.Validator;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

final class AutoMessagePlugin {

    private static final String PLUGIN_PREFIX = "AutoMessage";
    private static final int PLUGIN_METRICS_ID = 19487;

    private final Plugin plugin;
    private final Server server;
    private final PluginLogger logger;

    private ConfigManager configManager;

    private BukkitAudiences bukkitAudiences;
    private MessageService messageService;
    private TaskScheduler taskScheduler;

    private LiteCommands<?> liteCommands;
    private Metrics metrics;

    public AutoMessagePlugin(
            @NotNull final Plugin plugin,
            @NotNull final Server server,
            @NotNull final PluginLogger logger
    ) {
        this.plugin = Validator.notNull(plugin, "plugin");
        this.server = Validator.notNull(server, "server");
        this.logger = Validator.notNull(logger, "logger");
    }

    AutoMessagePlugin(@NotNull final Plugin plugin) {
        this(plugin, plugin.getServer(), new BukkitPluginLogger(plugin));
    }

    void enable(@NotNull PluginSettings settings) {
        Validator.notNull(settings, "settings");

        configManager = new ConfigManager(logger, plugin.getDataFolder());
        configManager.createAll(settings.configs());

        bukkitAudiences = BukkitAudiences.create(plugin);
        messageService = new MessageService(configManager.require(MessageConfig.class), bukkitAudiences);
        taskScheduler = new BukkitTaskScheduler(plugin, server.getScheduler());

        final MessageDispatcherConfig messageDispatcherConfig = configManager.require(MessageDispatcherConfig.class);
        final ScheduledMessagesConfig scheduledMessagesConfig = configManager.require(ScheduledMessagesConfig.class);

        final AudienceFilter audienceFilter = AudienceFilter.createDefault();
        final MessageSelector messageSelector = MessageSelectorFactory.create(messageDispatcherConfig.selector);

        final MessageDispatcher messageDispatcher = new MessageDispatcher(
                messageService,
                messageSelector,
                audienceFilter,
                () -> scheduledMessagesConfig.messages
        );

        MessageDispatcherTask messageDispatcherTask = new MessageDispatcherTask(server, messageDispatcherConfig, messageDispatcher);
        taskScheduler.runTimerAsync(messageDispatcherTask);

        liteCommands = LiteBukkitFactory.builder(PLUGIN_PREFIX, plugin, server)
                .invalidUsage(new InvalidUsageHandlerImpl(messageService))
                .missingPermission(new MissingPermissionsHandlerImpl(messageService))
                .result(Notice.class, new NoticeResultHandlerImpl(messageService))

                .commands(
                        new DisableCommand(messageDispatcherConfig, messageService),
                        new EnableCommand(messageDispatcherConfig, messageService),
                        new ReloadCommand(logger, configManager, taskScheduler, messageService)
                )

                .build();

        metrics = new Metrics(plugin, PLUGIN_METRICS_ID);

        logger.info("%s plugin enabled.", PLUGIN_PREFIX);
    }

    void disable() {
//        Validator.ifNotNull(configManager, manager -> {
//            manager.saveAll();
//            manager.clearAll();
//        });
        Validator.ifNotNull(bukkitAudiences, BukkitAudiences::close);
        Validator.ifNotNull(taskScheduler, TaskScheduler::shutdown);
        Validator.ifNotNull(liteCommands, LiteCommands::unregister);
        Validator.ifNotNull(metrics, Metrics::shutdown);

        logger.info("%s plugin disabled successfully.", PLUGIN_PREFIX);
    }
}
