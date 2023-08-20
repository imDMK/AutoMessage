package com.github.imdmk.automessage.notification.implementation;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationType;
import com.github.imdmk.automessage.util.StringUtil;

public record ChatNotification(String message) implements Notification {

    @Override
    public NotificationType type() {
        return NotificationType.CHAT;
    }

    @Override
    public String format() {
        return StringUtil.GRAY_COLOR + this.type().name() + ":"
                + StringUtil.NEW_LINE + StringUtil.GRAY_COLOR + "message: " + this.message;
    }

    @Override
    public String formatHover() {
        return "<hover:show_text:'" +  this.format() + "'>" + this.type().name();
    }
}
