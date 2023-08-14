package com.github.imdmk.automessage.notification.implementation;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationType;

public record ActionBarNotification(String message) implements Notification {

    @Override
    public NotificationType type() {
        return NotificationType.ACTIONBAR;
    }

    @Override
    public String format() {
        return this.type().name() + ", message: " + this.message;
    }
}
