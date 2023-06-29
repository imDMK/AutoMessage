package me.dmk.automessage.notification;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NotificationSender {

    private final AudienceProvider audienceProvider;
    private final MiniMessage miniMessage;

    public NotificationSender(AudienceProvider audienceProvider, MiniMessage miniMessage) {
        this.audienceProvider = audienceProvider;
        this.miniMessage = miniMessage;
    }

    public NotificationBuilder builder() {
        return new NotificationBuilder(this);
    }

    public void sendMessage(CommandSender sender, Notification notification) {
        if (sender instanceof Player player) {
            this.sendMessage(player, notification.getType(), notification.getMessage());
        }
        else {
            sender.sendMessage(notification.getMessage());
        }
    }

    public void sendMessage(Player player, NotificationType type, String message) {
        Component deserializedMessage = this.miniMessage.deserialize(message);
        Audience audience = this.audienceProvider.player(player.getUniqueId());

        this.sendMessage(audience, type, deserializedMessage);
    }

    public void sendMessage(Audience audience, Notification notification) {
        Component message = this.miniMessage.deserialize(notification.getMessage());

        this.sendMessage(audience, notification.getType(), message);
    }

    public void sendMessage(Audience audience, NotificationType type, Component message) {
        switch (type) {
            case CHAT -> audience.sendMessage(message);
            case ACTIONBAR -> audience.sendActionBar(message);
            case TITLE -> {
                Title title = Title.title(message, Component.empty(), Title.DEFAULT_TIMES);

                audience.showTitle(title);
            }
            case SUBTITLE -> {
                Title subtitle = Title.title(Component.empty(), message, Title.DEFAULT_TIMES);

                audience.showTitle(subtitle);
            }
            default -> throw new IllegalStateException("Unexpected notification type value: " + type);
        }
    }
}
