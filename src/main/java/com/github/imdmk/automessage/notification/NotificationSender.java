package com.github.imdmk.automessage.notification;

import com.github.imdmk.automessage.notification.implementation.SubTitleNotification;
import com.github.imdmk.automessage.notification.implementation.TitleNotification;
import com.github.imdmk.automessage.notification.implementation.bossbar.BossBarNotification;
import com.github.imdmk.automessage.text.Formatter;
import com.github.imdmk.automessage.util.ComponentUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NotificationSender {

    private final AudienceProvider audienceProvider;

    public NotificationSender(AudienceProvider audienceProvider) {
        this.audienceProvider = audienceProvider;
    }

    public Audience audiencePlayers() {
        return this.audienceProvider.players();
    }

    public void sendNotification(CommandSender sender, Notification notification) {
        Audience audience = this.createAudience(sender);
        Component message = ComponentUtil.deserialize(notification.message());

        this.processNotification(audience, notification, message);
    }

    public void sendNotification(Audience audience, Notification notification) {
        Component message = ComponentUtil.deserialize(notification.message());

        this.processNotification(audience, notification, message);
    }

    public void sendNotification(CommandSender sender, Notification notification, Formatter formatter) {
        Audience audience = this.createAudience(sender);
        Component message = ComponentUtil.deserialize(formatter.format(notification.message()));

        this.processNotification(audience, notification, message);
    }

    private void processNotification(Audience audience, Notification notification, Component message) {
        NotificationType type = notification.type();

        switch (type) {
            case CHAT -> audience.sendMessage(message);
            case ACTIONBAR -> audience.sendActionBar(message);

            case TITLE -> {
                TitleNotification titleNotification = (TitleNotification) notification;

                Title title = Title.title(message, Component.empty(), titleNotification.times());

                audience.showTitle(title);
            }

            case SUB_TITLE -> {
                SubTitleNotification subTitleNotification = (SubTitleNotification) notification;

                Title title = Title.title(Component.empty(), message, subTitleNotification.times());

                audience.showTitle(title);
            }

            case BOSS_BAR -> {
                BossBarNotification bossBarNotification = (BossBarNotification) notification;

                BossBar bossBar = bossBarNotification.create(message);

                audience.showBossBar(bossBar);
            }

            case DISABLED -> {
            }

            default -> throw new IllegalStateException("Unknown notification type: " + type);
        }
    }

    private Audience createAudience(CommandSender sender) {
        if (sender instanceof Player player) {
            return this.audienceProvider.player(player.getUniqueId());
        }

        return this.audienceProvider.console();
    }
}
