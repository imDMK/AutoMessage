package com.github.imdmk.automessage.notification;

import com.github.imdmk.automessage.notification.implementation.ActionBarNotification;
import com.github.imdmk.automessage.notification.implementation.ChatNotification;
import com.github.imdmk.automessage.notification.implementation.DisabledNotification;
import com.github.imdmk.automessage.notification.implementation.TitleNotification;
import com.github.imdmk.automessage.notification.implementation.bossbar.BossBarNotification;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class NotificationFormatter {

    private final Notification notification;
    private final HashMap<String, String> placeholders = new HashMap<>();

    public NotificationFormatter(Notification notification) {
        this.notification = notification;
    }

    @CheckReturnValue
    public NotificationFormatter placeholder(@Nonnull String from, String to) {
        this.placeholders.put(from, to);
        return this;
    }

    @CheckReturnValue
    public NotificationFormatter placeholder(@Nonnull String from, Iterable<? extends CharSequence> sequences) {
        this.placeholders.put(from, String.join(", ", sequences));
        return this;
    }

    @CheckReturnValue
    public <T> NotificationFormatter placeholder(@Nonnull String from, T to) {
        this.placeholders.put(from, to.toString());
        return this;
    }

    public String replacePlaceholders(String message) {
        AtomicReference<String> replacedMessage = new AtomicReference<>(message);

        for (Map.Entry<String, String> entry : this.placeholders.entrySet()) {
            replacedMessage.updateAndGet(string -> string.replace(entry.getKey(), entry.getValue()));
        }

        return replacedMessage.get();
    }

    public Notification build() {
        return switch (this.notification.type()) {
            case CHAT -> {
                ChatNotification chatNotification = (ChatNotification) this.notification;

                String message = this.replacePlaceholders(chatNotification.message());

                yield new ChatNotification(message);
            }

            case ACTIONBAR -> {
                ActionBarNotification actionBarNotification = (ActionBarNotification) this.notification;

                String message = this.replacePlaceholders(actionBarNotification.message());

                yield new ActionBarNotification(message);
            }

            case TITLE -> {
                TitleNotification titleNotification = (TitleNotification) this.notification;

                String title = this.replacePlaceholders(titleNotification.title());
                String subtitle = this.replacePlaceholders(titleNotification.subtitle());

                yield new TitleNotification(title, subtitle, titleNotification.times());
            }

            case BOSSBAR -> {
                BossBarNotification bossBarNotification = (BossBarNotification) this.notification;

                String name = this.replacePlaceholders(bossBarNotification.name());

                yield new BossBarNotification(name, bossBarNotification.time(), bossBarNotification.progress(), bossBarNotification.color(), bossBarNotification.overlay());
            }

            case DISABLED -> new DisabledNotification();
        };
    }
}
