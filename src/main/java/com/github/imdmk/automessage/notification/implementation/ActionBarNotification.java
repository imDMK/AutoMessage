package com.github.imdmk.automessage.notification.implementation;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationType;
import com.github.imdmk.automessage.util.StringUtil;

public record ActionBarNotification(String message) implements Notification {

    @Override
    public NotificationType type() {
        return NotificationType.ACTIONBAR;
    }

    @Override
    public String format() {
        return StringUtil.GRAY_COLOR + this.type().name() + ":"
                + StringUtil.NEW_LINE + StringUtil.GRAY_COLOR + "message: " + this.message;
    }
}
