package me.dmk.automessage;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.adventure.platform.LiteBukkitAdventurePlatformFactory;
import dev.rollczi.litecommands.bukkit.tools.BukkitOnlyPlayerContextual;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import me.dmk.automessage.command.AutoMessageCommand;
import me.dmk.automessage.command.argument.NotificationArgument;
import me.dmk.automessage.command.argument.NotificationTypeArgument;
import me.dmk.automessage.command.editor.CommandPermissionEditor;
import me.dmk.automessage.command.handler.NotificationHandler;
import me.dmk.automessage.command.handler.PermissionHandler;
import me.dmk.automessage.command.handler.UsageHandler;
import me.dmk.automessage.configuration.PluginConfiguration;
import me.dmk.automessage.notification.Notification;
import me.dmk.automessage.notification.NotificationSender;
import me.dmk.automessage.notification.NotificationType;
import me.dmk.automessage.task.AutoMessageTask;
import me.dmk.automessage.task.scheduler.TaskScheduler;
import me.dmk.automessage.task.scheduler.TaskSchedulerImpl;
import me.dmk.automessage.util.DurationUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.time.Duration;
import java.time.Instant;

public class AutoMessage extends JavaPlugin {

    private PluginConfiguration pluginConfiguration;

    private BukkitAudiences bukkitAudiences;
    private NotificationSender notificationSender;

    private LiteCommands<CommandSender> liteCommands;

    @Override
    public void onEnable() {
        Instant start = Instant.now();

        this.pluginConfiguration = ConfigManager.create(PluginConfiguration.class, (it) -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new SerdesCommons());
            it.withBindFile(new File(this.getDataFolder(), "configuration.yml"));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

        this.bukkitAudiences = BukkitAudiences.create(this);
        this.notificationSender = new NotificationSender(this.bukkitAudiences, MiniMessage.miniMessage());

        TaskScheduler taskScheduler = new TaskSchedulerImpl(this, this.getServer());
        taskScheduler.runTimerAsync(new AutoMessageTask(this.pluginConfiguration, this.bukkitAudiences, this.notificationSender), 0L, DurationUtil.toTicks(this.pluginConfiguration.autoMessagesDelay));

        if (this.pluginConfiguration.autoMessageCommandEnabled) {
            this.liteCommands = this.registerLiteCommands();
        }

        Duration startBetweenNow = Duration.between(start, Instant.now());
        this.getLogger().info("Enabled plugin in " + startBetweenNow.toMillis() + "ms.");
    }

    @Override
    public void onDisable() {
        if (this.liteCommands != null) {
            this.liteCommands.getPlatform().unregisterAll();
        }

        this.bukkitAudiences.close();
    }

    private LiteCommands<CommandSender> registerLiteCommands() {
        return LiteBukkitAdventurePlatformFactory.builder(this.getServer(), this.getName(), false, this.bukkitAudiences, true)
                .contextualBind(Player.class, new BukkitOnlyPlayerContextual<>("Command only for player"))

                .argument(NotificationType.class, new NotificationTypeArgument(this.pluginConfiguration))
                .argument(Notification.class, new NotificationArgument(this.pluginConfiguration, this.notificationSender))

                .invalidUsageHandler(new UsageHandler(this.pluginConfiguration, this.notificationSender))
                .permissionHandler(new PermissionHandler(this.pluginConfiguration, this.notificationSender))
                .resultHandler(Notification.class, new NotificationHandler(this.notificationSender))

                .commandInstance(new AutoMessageCommand(this.pluginConfiguration, this.notificationSender))
                .commandEditor(AutoMessageCommand.class, new CommandPermissionEditor(this.pluginConfiguration))

                .register();
    }
}
