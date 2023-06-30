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

    public void broadcast(Notification notification) {
        Audience audience = this.audienceProvider.players();

        NotificationType type = notification.getType();
        Component message = this.miniMessage.deserialize(notification.getMessage());

        this.sendMessage(audience, type, message);
    }

    public void sendMessage(CommandSender sender, Notification notification) {
        Audience audience = this.createAudience(sender);

        NotificationType type = notification.getType();
        Component message = this.miniMessage.deserialize(notification.getMessage());

        this.sendMessage(audience, type, message);
    }

    public void sendMessage(CommandSender sender, NotificationType type, String message) {
        Audience audience = this.createAudience(sender);
        Component deserializedMessage = this.miniMessage.deserialize(message);

        this.sendMessage(audience, type, deserializedMessage);
    }

    public Audience createAudience(CommandSender sender) {
        if (sender instanceof Player player) {
            return this.audienceProvider.player(player.getUniqueId());
        }

        return this.audienceProvider.console();
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
