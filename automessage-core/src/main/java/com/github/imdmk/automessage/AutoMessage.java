package com.github.imdmk.automessage;

import com.github.imdmk.automessage.config.ConfigManager;
import com.github.imdmk.automessage.config.ConfigReloadService;
import com.github.imdmk.automessage.language.LanguageRegistry;
import com.github.imdmk.automessage.message.MessageService;
import com.github.imdmk.automessage.platform.Platform;
import com.github.imdmk.automessage.platform.discord.DiscordWebhookConfig;
import com.github.imdmk.automessage.platform.discord.DiscordWebhookService;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolver;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;
import com.github.imdmk.automessage.scheduled.ScheduledMessageRepository;
import com.github.imdmk.automessage.scheduled.ScheduledMessageSender;
import com.github.imdmk.automessage.scheduled.ScheduledMessagesConfig;
import com.github.imdmk.automessage.scheduled.audience.filter.AudienceFilter;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceContext;
import com.github.imdmk.automessage.scheduled.dispatcher.DispatchObserver;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcher;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcherConfig;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcherService;
import com.github.imdmk.automessage.scheduled.dispatcher.ScheduledMessageDispatcherFactory;
import com.github.imdmk.automessage.scheduled.selector.MessageSelectorProvider;
import com.github.imdmk.automessage.scheduled.selector.MessageSelectorType;
import com.github.imdmk.automessage.scheduled.trigger.MessageTriggerService;
import com.github.imdmk.automessage.scheduled.trigger.PlayerCountMilestones;

import java.io.File;

public final class AutoMessage {

    public static final String NAME = "AutoMessage";
    private static final String DONATE_URL = "https://paypal.me/dominiksuliga";

    private final PluginLogger logger;
    private final Platform platform;
    private final ConfigManager configManager;
    private final ConfigReloadService configReloadService;

    private final LanguageRegistry languages;
    private final MessageService messageService;
    private final MessageDispatcherConfig dispatcherConfig;
    private final ScheduledMessageRepository messageRepository;
    private final ScheduledMessageSender messageSender;
    private final MessageDispatcherService dispatcherService;
    private final MessageTriggerService triggerService;
    private final DispatchObserver dispatchObserver;

    public AutoMessage(
            Platform platform,
            PluginLogger logger,
            File dataFolder,
            ExternalPlaceholderResolver placeholders
    ) {
        this.platform = platform;
        this.logger = logger;

        // Written for this platform only: a proxy has no worlds and keeps no playtime, so the
        // options for those are left out of the file rather than written and quietly ignored.
        this.configManager = new ConfigManager(logger, dataFolder, platform.capabilities());

        // config.yml first: it names the language to fall back to.
        this.dispatcherConfig = configManager.create(MessageDispatcherConfig.class);
        final ScheduledMessagesConfig scheduledMessages = configManager.create(ScheduledMessagesConfig.class);

        this.languages = LanguageRegistry.load(configManager, logger, () -> dispatcherConfig.fallbackLanguage);
        this.configReloadService = new ConfigReloadService(configManager);

        // Registered first, so a message rendered by a later listener already sees the languages
        // this reload discovered.
        configReloadService.register(languages);

        final TaskScheduler scheduler = platform.scheduler();
        final ViewerRegistry viewers = platform.viewers();
        final AudienceContext audienceContext = AudienceContext.of(viewers, platform.playtime());

        // A boss bar hides itself on the plugin's own scheduler, which is the only clock the
        // notice module is given - it has no way to tell the time by itself, by design.
        this.messageService = new MessageService(
                languages,
                (delay, action) -> scheduler.runLaterSync(action, delay)
        );

        this.messageRepository = ScheduledMessageRepository.config(scheduledMessages, configReloadService);
        this.messageSender = new ScheduledMessageSender(
                viewers, logger, messageService, languages, placeholders
        );

        final DiscordWebhookConfig discordConfig = configManager.create(DiscordWebhookConfig.class);

        // One observer shared by every channel: mirroring is a property of the announcement, not
        // of the stream it happened to travel on.
        this.dispatchObserver = DiscordWebhookService.create(viewers, languages, logger, discordConfig);

        final ScheduledMessageDispatcherFactory dispatcherFactory =
                selector -> new MessageDispatcher(
                        messageSender,
                        selector,
                        AudienceFilter.ruleFilter(),
                        audienceContext,
                        dispatchObserver
                );

        this.dispatcherService = new MessageDispatcherService(
                logger, viewers, scheduler, dispatcherConfig, messageRepository, dispatcherFactory
        );

        dispatcherService.start();
        configReloadService.register(dispatcherService);

        // A trigger dispatches the one message its event names, so this dispatcher's selector is
        // never consulted. It still gets a real one rather than a trap that would only fire if
        // some later change started rotating through triggered messages.
        final MessageDispatcher triggerDispatcher = dispatcherFactory.create(
                new MessageSelectorProvider(() -> MessageSelectorType.SEQUENTIAL)
        );

        this.triggerService = new MessageTriggerService(
                viewers,
                scheduler,
                messageRepository,
                triggerDispatcher,
                AudienceFilter.ruleFilter(),
                audienceContext,
                new PlayerCountMilestones()
        );

        logger.info("%s enabled on %s.", NAME, platform.name());
        logger.info("Enjoying %s? You can support its development at %s - thank you!", NAME, DONATE_URL);
    }

    public void shutdown() {
        close("stop the dispatcher", dispatcherService::stop);
        close("save the configuration", configManager::saveAll);
        close("release the configuration", configManager::clearAll);
        close("stop the scheduler", platform.scheduler()::shutdown);
        close("close the Discord webhook", dispatchObserver::shutdown);

        logger.info("%s disabled successfully.", NAME);
    }

    private void close(String what, Runnable step) {
        try {
            step.run();
        } catch (RuntimeException exception) {
            logger.error(exception, "Failed to %s while disabling %s.", what, NAME);
        }
    }

    public PluginLogger logger() {
        return logger;
    }

    public LanguageRegistry languages() {
        return languages;
    }

    public MessageService messageService() {
        return messageService;
    }

    public MessageDispatcherConfig dispatcherConfig() {
        return dispatcherConfig;
    }

    public ScheduledMessageRepository messageRepository() {
        return messageRepository;
    }

    public ScheduledMessageSender messageSender() {
        return messageSender;
    }

    public ConfigReloadService configReloadService() {
        return configReloadService;
    }

    public MessageTriggerService triggerService() {
        return triggerService;
    }

    public TaskScheduler scheduler() {
        return platform.scheduler();
    }
}
