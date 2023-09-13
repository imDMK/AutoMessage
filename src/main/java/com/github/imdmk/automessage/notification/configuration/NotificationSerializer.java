package com.github.imdmk.automessage.notification.configuration;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationType;
import com.github.imdmk.automessage.notification.implementation.ActionBarNotification;
import com.github.imdmk.automessage.notification.implementation.ChatNotification;
import com.github.imdmk.automessage.notification.implementation.DisabledNotification;
import com.github.imdmk.automessage.notification.implementation.SubTitleNotification;
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
        String message = notification.message();

        data.add("type", type, NotificationType.class);

        switch (type) {
            case CHAT, ACTIONBAR -> data.add("message", message, String.class);

            case TITLE -> {
                TitleNotification titleNotification = (TitleNotification) notification;

                data.add("title", message, String.class);
                data.add("times", titleNotification.times(), Title.Times.class);
            }

            case SUB_TITLE -> {
                SubTitleNotification subTitleNotification = (SubTitleNotification) notification;

                data.add("subtitle", subTitleNotification.message(), String.class);
                data.add("times", subTitleNotification.times(), Title.Times.class);
            }

            case BOSS_BAR -> {
                BossBarNotification bossBarNotification = (BossBarNotification) notification;

                data.add("name", bossBarNotification.message(), String.class);
                data.add("time", bossBarNotification.time(), Duration.class);
                data.add("progress", bossBarNotification.progress(), float.class);
                data.add("timeChangesProgress", bossBarNotification.timeChangesProgress(), boolean.class);
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
        String message = data.get("message", String.class);
        NotificationType type = data.get("type", NotificationType.class);

        return switch (type) {
            case CHAT -> new ChatNotification(message);
            case ACTIONBAR -> new ActionBarNotification(message);

            case TITLE -> {
                Title.Times times = this.getOptionalTitleTimes(data).orElse(Title.DEFAULT_TIMES);

                yield new TitleNotification(message, times);
            }

            case SUB_TITLE -> {
                Title.Times times = this.getOptionalTitleTimes(data).orElse(Title.DEFAULT_TIMES);

                yield new SubTitleNotification(message, times);
            }

            case BOSS_BAR -> {
                Duration time = data.get("time", Duration.class);
                float progress = data.get("progress", float.class);
                boolean timeChangesProgress = data.get("timeChangesProgress", boolean.class);
                BossBar.Color color = data.get("color", BossBar.Color.class);
                BossBar.Overlay overlay = data.get("overlay", BossBar.Overlay.class);

                yield new BossBarNotification(message, time, progress, timeChangesProgress, color, overlay);
            }

            case DISABLED -> new DisabledNotification();
        };
    }

    private Optional<Title.Times> getOptionalTitleTimes(DeserializationData data) {
        return Optional.ofNullable(data.get("times", Title.Times.class));
    }
}
