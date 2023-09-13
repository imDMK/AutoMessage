package com.github.imdmk.automessage.notification;

import com.github.imdmk.automessage.notification.implementation.SubTitleNotification;
import com.github.imdmk.automessage.notification.implementation.TitleNotification;
import com.github.imdmk.automessage.notification.implementation.bossbar.BossBarNotification;
import com.github.imdmk.automessage.notification.implementation.bossbar.audience.BossBarAudience;
import com.github.imdmk.automessage.notification.implementation.bossbar.audience.BossBarAudienceManager;
import com.github.imdmk.automessage.util.ComponentUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

public class NotificationSender {

    private final AudienceProvider audienceProvider;
    private final BossBarAudienceManager bossBarAudienceManager;

    public NotificationSender(AudienceProvider audienceProvider, BossBarAudienceManager bossBarAudienceManager) {
        this.audienceProvider = audienceProvider;
        this.bossBarAudienceManager = bossBarAudienceManager;
    }

    public void broadcast(Notification notification) {
        Audience audience = this.audienceProvider.players();

        this.sendNotification(audience, notification, null);
    }

    public void sendNotification(CommandSender sender, Notification notification) {
        Audience audience = this.createAudience(sender);

        this.sendNotification(audience, notification, null);
    }

    public void sendNotification(CommandSender sender, Notification notification, Formatter formatter) {
        Audience audience = this.createAudience(sender);

        this.sendNotification(audience, notification, formatter);
    }

    public void sendNotification(Audience audience, Notification notification, Formatter formatter) {
        Component message = ComponentUtil.deserialize(this.format(notification.message(), formatter));
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

                BossBar bossBar = BossBar.bossBar(message, bossBarNotification.progress(), bossBarNotification.color(), bossBarNotification.overlay());

                audience.showBossBar(bossBar);

                BossBarAudience bossBarAudience = new BossBarAudience(bossBar, audience, bossBarNotification);
                this.bossBarAudienceManager.add(bossBarAudience);
            }

            case DISABLED -> {
            }

            default -> throw new IllegalStateException("Unknown notification type: " + type);
        }
    }

    public String format(String message, Formatter formatter) {
        if (formatter == null) {
            return message;
        }

        return formatter.format(message);
    }

    public Audience createAudience(CommandSender sender) {
        if (sender instanceof Player player) {
            return this.audienceProvider.player(player.getUniqueId());
        }

        return this.audienceProvider.console();
    }
}
