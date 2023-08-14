package com.github.imdmk.automessage.notification.implementation;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationType;

public class DisabledNotification implements Notification {

    @Override
    public NotificationType type() {
        return NotificationType.DISABLED;
    }

    @Override
    public String format() {
        return this.type().name();
    }
}
