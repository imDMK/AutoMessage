package me.dmk.automessage.notification;

import com.google.errorprone.annotations.CheckReturnValue;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class NotificationBuilder {

    private final NotificationSender notificationSender;

    private NotificationType type;
    private String message;

    private final Map<String, String> placeholders = new HashMap<>();

    public NotificationBuilder(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

    @CheckReturnValue
    public NotificationBuilder fromNotification(@Nonnull Notification notification) {
        this.type = notification.getType();
        this.message = notification.getMessage();
        return this;
    }

    @CheckReturnValue
    public NotificationBuilder type(@Nonnull NotificationType type) {
        this.type = type;
        return this;
    }

    @CheckReturnValue
    public NotificationBuilder message(@Nonnull String message) {
        this.message = message;
        return this;
    }

    @CheckReturnValue
    public NotificationBuilder placeholder(@Nonnull String from, String to) {
        this.placeholders.put(from, to);
        return this;
    }

    @CheckReturnValue
    public NotificationBuilder placeholder(@Nonnull String from, Iterable<? extends CharSequence> sequences) {
        this.placeholders.put(from, String.join(", ", sequences));
        return this;
    }

    @CheckReturnValue
    public <T> NotificationBuilder placeholder(@Nonnull String from, T to) {
        this.placeholders.put(from, to.toString());
        return this;
    }

    public Notification build() {
        this.replacePlaceholders();

        return new Notification(this.type, this.message);
    }

    public void send(Player player) {
        this.replacePlaceholders();

        this.notificationSender.sendMessage(player, this.type, this.message);
    }

    public void send(CommandSender sender) {
        if (sender instanceof Player player) {
            this.send(player);
            return;
        }

        this.replacePlaceholders();

        sender.sendMessage(this.message);
    }

    private void replacePlaceholders() {
        for (Map.Entry<String, String> entry : this.placeholders.entrySet()) {
            this.message = this.message.replace(entry.getKey(), entry.getValue());
        }
    }
}
