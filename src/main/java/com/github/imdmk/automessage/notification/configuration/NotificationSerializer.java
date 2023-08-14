package com.github.imdmk.automessage.notification.configuration;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationType;
import com.github.imdmk.automessage.notification.implementation.ActionBarNotification;
import com.github.imdmk.automessage.notification.implementation.ChatNotification;
import com.github.imdmk.automessage.notification.implementation.DisabledNotification;
import com.github.imdmk.automessage.notification.implementation.TitleNotification;
import com.github.imdmk.automessage.notification.implementation.bossbar.BossBarNotification;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.title.Title;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;
import java.util.Optional;

public class NotificationSerializer implements ObjectSerializer<Notification> {
    
    @Override
    public boolean supports(@NonNull Class<? super Notification> type) {
        return Notification.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull Notification notification, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        NotificationType type = notification.type();

        data.add("type", type, NotificationType.class);

        switch (type) {
            case CHAT -> {
                ChatNotification chatNotification = (ChatNotification) notification;

                data.add("message", chatNotification.message(), String.class);
            }

            case ACTIONBAR -> {
                ActionBarNotification actionBarNotification = (ActionBarNotification) notification;

                data.add("message", actionBarNotification.message(), String.class);
            }

            case TITLE -> {
                TitleNotification titleNotification = (TitleNotification) notification;

                data.add("title", titleNotification.title(), String.class);
                data.add("subtitle", titleNotification.subtitle(), String.class);

                data.add("times", titleNotification.times(), Title.Times.class);
            }

            case BOSSBAR -> {
                BossBarNotification bossBarNotification = (BossBarNotification) notification;

                data.add("name", bossBarNotification.name(), String.class);
                data.add("time", bossBarNotification.time(), Duration.class);
                data.add("progress", bossBarNotification.progress(), float.class);
                data.add("color", bossBarNotification.color(), BossBar.Color.class);
                data.add("overlay", bossBarNotification.overlay(), BossBar.Overlay.class);
            }

            case DISABLED -> {
            }

            default -> throw new IllegalStateException("Unexpected notification type: " + type);
        }
    }

    @Override
    public Notification deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        NotificationType type = data.get("type", NotificationType.class);

        switch (type) {
            case CHAT -> {
                String message = data.get("message", String.class);

                return new ChatNotification(message);
            }

            case ACTIONBAR -> {
                String message = data.get("message", String.class);

                return new ActionBarNotification(message);
            }

            case TITLE -> {
                String title = data.get("title", String.class);
                String subtitle = data.get("subtitle", String.class);

                Title.Times times = Optional.ofNullable(data.get("times", Title.Times.class))
                        .orElse(Title.DEFAULT_TIMES);

                return new TitleNotification(title, subtitle, times);
            }

            case BOSSBAR -> {
                String name = data.get("name", String.class);
                Duration time = data.get("time", Duration.class);
                float progress = data.get("progress", float.class);
                BossBar.Color color = data.get("color", BossBar.Color.class);
                BossBar.Overlay overlay =  data.get("overlay", BossBar.Overlay.class);

                return new BossBarNotification(name, time, progress, color, overlay);
            }

            case DISABLED -> {
                return new DisabledNotification();
            }

            default -> throw new IllegalStateException("Unexpected notification type: " + type);
        }
    }
}
