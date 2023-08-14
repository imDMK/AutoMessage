package com.github.imdmk.automessage.notification;

import com.github.imdmk.automessage.notification.implementation.ActionBarNotification;
import com.github.imdmk.automessage.notification.implementation.ChatNotification;
import com.github.imdmk.automessage.notification.implementation.TitleNotification;
import com.github.imdmk.automessage.notification.implementation.bossbar.BossBarNotification;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NotificationSender {

    private final AudienceProvider audienceProvider;
    private final MiniMessage miniMessage;

    public NotificationSender(AudienceProvider audienceProvider) {
        this.audienceProvider = audienceProvider;
        this.miniMessage = MiniMessage.miniMessage();
    }

    public void broadcast(Notification notification) {
        Audience audience = this.audienceProvider.players();

        this.sendNotification(audience, notification);
    }

    public void sendNotification(CommandSender sender, Notification notification) {
        Audience audience = this.createAudience(sender);

        this.sendNotification(audience, notification);
    }

    public void sendNotification(Audience audience, Notification notification) {
        switch (notification.type()) {
            case CHAT -> {
                ChatNotification chatNotification = (ChatNotification) notification;

                Component message = this.miniMessage.deserialize(chatNotification.message());

                audience.sendMessage(message);
            }

            case ACTIONBAR -> {
                ActionBarNotification actionBarNotification = (ActionBarNotification) notification;

                Component message = this.miniMessage.deserialize(actionBarNotification.message());

                audience.sendActionBar(message);
            }

            case TITLE -> {
                TitleNotification titleNotification = (TitleNotification) notification;

                Title titleMessage = titleNotification.create();

                audience.showTitle(titleMessage);
            }

            case BOSSBAR -> {
                BossBarNotification bossBarNotification = (BossBarNotification) notification;

                BossBar bossBar = bossBarNotification.create();

                audience.showBossBar(bossBar);
            }

            default -> throw new IllegalStateException("Unexpected notification type: " + notification.type());
        }
    }

    public Audience createAudience(CommandSender sender) {
        if (sender instanceof Player player) {
            return this.audienceProvider.player(player.getUniqueId());
        }

        return this.audienceProvider.console();
    }
}
