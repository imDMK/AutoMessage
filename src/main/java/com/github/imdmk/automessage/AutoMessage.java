package com.github.imdmk.automessage;

import com.github.imdmk.automessage.command.argument.BossBarProgressArgument;
import com.github.imdmk.automessage.command.argument.NotificationArgument;
import com.github.imdmk.automessage.command.argument.NotificationTypeArgument;
import com.github.imdmk.automessage.command.handler.MissingPermissionHandler;
import com.github.imdmk.automessage.command.handler.NotificationHandler;
import com.github.imdmk.automessage.command.handler.UsageHandler;
import com.github.imdmk.automessage.command.implementation.AutoMessageCreateCommand;
import com.github.imdmk.automessage.command.implementation.AutoMessageListCommand;
import com.github.imdmk.automessage.command.implementation.AutoMessageRemoveCommand;
import com.github.imdmk.automessage.command.implementation.AutoMessageSendCommand;
import com.github.imdmk.automessage.command.implementation.AutoMessageStateCommand;
import com.github.imdmk.automessage.command.implementation.editor.AutoMessageCommandEditor;
import com.github.imdmk.automessage.configuration.ConfigurationFactory;
import com.github.imdmk.automessage.configuration.implementation.PluginConfiguration;
import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.NotificationType;
import com.github.imdmk.automessage.notification.implementation.bossbar.audience.BossBarAudienceManager;
import com.github.imdmk.automessage.notification.implementation.bossbar.audience.BossBarAudienceTask;
import com.github.imdmk.automessage.notification.task.AutoNotificationTask;
import com.github.imdmk.automessage.scheduler.TaskScheduler;
import com.github.imdmk.automessage.scheduler.TaskSchedulerImpl;
import com.github.imdmk.automessage.update.UpdateListener;
import com.github.imdmk.automessage.update.UpdateService;
import com.github.imdmk.automessage.util.DurationUtil;
import com.google.common.base.Stopwatch;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.adventure.platform.LiteBukkitAdventurePlatformFactory;
import dev.rollczi.litecommands.bukkit.tools.BukkitOnlyPlayerContextual;
import dev.rollczi.litecommands.bukkit.tools.BukkitPlayerArgument;
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
import java.util.stream.Stream;

public class AutoMessage  {

    private final Server server;

    private final PluginConfiguration pluginConfiguration;

    private final BukkitAudiences bukkitAudiences;
    private final NotificationSender notificationSender;

    private LiteCommands<CommandSender> liteCommands;

    private final Metrics metrics;

    public AutoMessage(Plugin plugin) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Logger logger = plugin.getLogger();

        this.server = plugin.getServer();

        /* Configuration */
        File dataFolder = plugin.getDataFolder();
        this.pluginConfiguration = ConfigurationFactory.create(PluginConfiguration.class, new File(dataFolder, "configuration.yml"));

        /* Managers */
        BossBarAudienceManager bossBarAudienceManager = new BossBarAudienceManager();

        /* Services */
        UpdateService updateService = new UpdateService(plugin.getDescription());

        /* Adventure */
        this.bukkitAudiences = BukkitAudiences.create(plugin);
        this.notificationSender = new NotificationSender(this.bukkitAudiences, bossBarAudienceManager);

        /* Tasks */
        TaskScheduler taskScheduler = new TaskSchedulerImpl(plugin, this.server);

        taskScheduler.runTimerAsync(new AutoNotificationTask(this.pluginConfiguration, this.notificationSender), 0L, DurationUtil.toTicks(this.pluginConfiguration.autoMessagesDelay));
        taskScheduler.runTimerAsync(new BossBarAudienceTask(bossBarAudienceManager), 0L, DurationUtil.toTicks(Duration.ofSeconds(1)));

        /* Listeners */
        Stream.of(
                new UpdateListener(this.pluginConfiguration, this.notificationSender, updateService, taskScheduler)
        ).forEach(listener -> this.server.getPluginManager().registerEvents(listener, plugin));

        /* Commands */
        if (this.pluginConfiguration.commandSettings.autoMessageEnabled) {
            this.liteCommands = this.registerLiteCommands();
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

    private LiteCommands<CommandSender> registerLiteCommands() {
        return LiteBukkitAdventurePlatformFactory.builder(this.server, "AutoMessage", false, this.bukkitAudiences, true)
                .contextualBind(Player.class, new BukkitOnlyPlayerContextual<>("Command only for player"))

                .argument(Player.class, new BukkitPlayerArgument<>(this.server, this.pluginConfiguration.notificationSettings.playerNotFoundNotification))
                .argument(NotificationType.class, new NotificationTypeArgument(this.pluginConfiguration.notificationSettings))
                .argument(Notification.class, new NotificationArgument(this.pluginConfiguration, this.pluginConfiguration.notificationSettings))

                .argument(float.class, "bossBarProgress", new BossBarProgressArgument(this.pluginConfiguration.notificationSettings))

                .invalidUsageHandler(new UsageHandler(this.pluginConfiguration.notificationSettings, this.notificationSender))
                .permissionHandler(new MissingPermissionHandler(this.pluginConfiguration.notificationSettings, this.notificationSender))
                .resultHandler(Notification.class, new NotificationHandler(this.notificationSender))

                .commandInstance(
                        new AutoMessageStateCommand(this.pluginConfiguration, this.pluginConfiguration.notificationSettings, this.notificationSender),
                        new AutoMessageCreateCommand(this.pluginConfiguration, this.pluginConfiguration.notificationSettings, this.notificationSender),
                        new AutoMessageListCommand(this.pluginConfiguration, this.pluginConfiguration.notificationSettings, this.notificationSender),
                        new AutoMessageSendCommand(this.pluginConfiguration.notificationSettings, this.notificationSender),
                        new AutoMessageRemoveCommand(this.pluginConfiguration, this.pluginConfiguration.notificationSettings, this.notificationSender)
                )

                .commandEditor("automessage", new AutoMessageCommandEditor(this.pluginConfiguration.commandSettings))

                .register();
    }
}
