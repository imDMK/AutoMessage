package com.github.imdmk.automessage.notification.implementation;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationType;
import com.github.imdmk.automessage.util.StringUtil;
import net.kyori.adventure.title.Title;

public record TitleNotification(String message, Title.Times times) implements Notification {

    public TitleNotification(String message) {
        this(message, Title.DEFAULT_TIMES);
    }

    @Override
    public NotificationType type() {
        return NotificationType.TITLE;
    }

    @Override
    public String format() {
        return StringUtil.GRAY_COLOR + this.type().name() + ":"
                + StringUtil.NEW_LINE + StringUtil.GRAY_COLOR + "title: " + this.message;
    }
}
