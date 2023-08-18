package com.github.imdmk.automessage;

import com.github.imdmk.automessage.command.AutoMessageCommand;
import com.github.imdmk.automessage.command.AutoMessageCommandEditor;
import com.github.imdmk.automessage.command.AutoMessageCreateCommand;
import com.github.imdmk.automessage.command.AutoMessageListCommand;
import com.github.imdmk.automessage.command.AutoMessageRemoveCommand;
import com.github.imdmk.automessage.command.argument.BossBarProgressArgument;
import com.github.imdmk.automessage.command.argument.NotificationArgument;
import com.github.imdmk.automessage.command.argument.NotificationTypeArgument;
import com.github.imdmk.automessage.command.handler.MissingPermissionHandler;
import com.github.imdmk.automessage.command.handler.NotificationHandler;
import com.github.imdmk.automessage.command.handler.UsageHandler;
import com.github.imdmk.automessage.configuration.PluginConfiguration;
import com.github.imdmk.automessage.configuration.serializer.TitleTimesSerializer;
import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.NotificationType;
import com.github.imdmk.automessage.notification.configuration.NotificationSerializer;
import com.github.imdmk.automessage.notification.implementation.bossbar.audience.BossBarAudienceManager;
import com.github.imdmk.automessage.notification.implementation.bossbar.audience.BossBarAudienceTask;
import com.github.imdmk.automessage.notification.task.AutoNotificationTask;
import com.github.imdmk.automessage.scheduler.TaskScheduler;
import com.github.imdmk.automessage.scheduler.TaskSchedulerImpl;
import com.github.imdmk.automessage.update.UpdateService;
import com.github.imdmk.automessage.util.DurationUtil;
import com.google.common.base.Stopwatch;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.adventure.platform.LiteBukkitAdventurePlatformFactory;
import dev.rollczi.litecommands.bukkit.tools.BukkitOnlyPlayerContextual;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class AutoMessage  {

    private final Server server;

    private final PluginConfiguration pluginConfiguration;

    private final BossBarAudienceManager bossBarAudienceManager;

    private final BukkitAudiences bukkitAudiences;
    private final NotificationSender notificationSender;

    private final TaskScheduler taskScheduler;

    private LiteCommands<CommandSender> liteCommands;

    private final Metrics metrics;

    public AutoMessage(Plugin plugin) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Logger logger = plugin.getLogger();

        this.server = plugin.getServer();

        /* Configuration */
        this.pluginConfiguration = this.createConfiguration(plugin.getDataFolder());

        /* Managers */
        this.bossBarAudienceManager = new BossBarAudienceManager();

        /* Adventure */
        this.bukkitAudiences = BukkitAudiences.create(plugin);
        this.notificationSender = new NotificationSender(this.bukkitAudiences, this.bossBarAudienceManager);

        /* Tasks */
        this.taskScheduler = new TaskSchedulerImpl(plugin, this.server);

        this.taskScheduler.runTimerAsync(new AutoNotificationTask(this.pluginConfiguration.notificationConfiguration, this.notificationSender), 10L, DurationUtil.toTicks(this.pluginConfiguration.notificationConfiguration.autoMessagesDelay));
        this.taskScheduler.runTimerAsync(new BossBarAudienceTask(this.bossBarAudienceManager), 0L, DurationUtil.toTicks(Duration.ofSeconds(1)));

        /* Commands */
        if (this.pluginConfiguration.autoMessageCommandEnabled) {
            this.liteCommands = this.registerLiteCommands();
        }

        /* Update service */
        if (this.pluginConfiguration.checkForUpdate) {
            UpdateService updateService = new UpdateService(plugin.getDescription(), logger);
            this.taskScheduler.runLaterAsync(updateService::check, DurationUtil.toTicks(Duration.ofSeconds(3)));
        }

        /* Metrics */
        this.metrics = new Metrics((JavaPlugin) plugin, 19487);

        logger.info("Enabled plugin in " + stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) + "ms.");
    }

    public void onDisable() {
        this.bukkitAudiences.close();

        if (this.liteCommands != null) {
            this.liteCommands.getPlatform().unregisterAll();
        }

        this.metrics.shutdown();
    }

    private PluginConfiguration createConfiguration(File dataFolder) {
        return ConfigManager.create(PluginConfiguration.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesCommons());
            it.withSerdesPack(registry -> {
                registry.register(new TitleTimesSerializer());
                registry.register(new NotificationSerializer());
            });
            it.withBindFile(new File(dataFolder, "configuration.yml"));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });
    }

    private LiteCommands<CommandSender> registerLiteCommands() {
        return LiteBukkitAdventurePlatformFactory.builder(this.server, "AutoMessage", false, this.bukkitAudiences, true)
                .contextualBind(Player.class, new BukkitOnlyPlayerContextual<>("Command only for player"))

                .argument(float.class, "bossBarProgress", new BossBarProgressArgument(this.pluginConfiguration.notificationConfiguration))
                .argument(NotificationType.class, new NotificationTypeArgument(this.pluginConfiguration.notificationConfiguration))
                .argument(Notification.class, new NotificationArgument(this.pluginConfiguration.notificationConfiguration))

                .invalidUsageHandler(new UsageHandler(this.pluginConfiguration.notificationConfiguration, this.notificationSender))
                .permissionHandler(new MissingPermissionHandler(this.pluginConfiguration.notificationConfiguration, this.notificationSender))
                .resultHandler(Notification.class, new NotificationHandler(this.notificationSender))

                .commandInstance(
                        new AutoMessageCommand(this.pluginConfiguration.notificationConfiguration, this.notificationSender),
                        new AutoMessageCreateCommand(this.pluginConfiguration.notificationConfiguration, this.notificationSender),
                        new AutoMessageListCommand(this.pluginConfiguration.notificationConfiguration, this.notificationSender),
                        new AutoMessageRemoveCommand(this.pluginConfiguration.notificationConfiguration, this.notificationSender)
                )

                .commandEditor("automessage", new AutoMessageCommandEditor(this.pluginConfiguration))

                .register();
    }
}
