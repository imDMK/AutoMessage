package com.github.imdmk.automessage.notification.implementation;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationType;
import com.github.imdmk.automessage.util.StringUtil;

public class DisabledNotification implements Notification {

    @Override
    public String message() {
        return null;
    }

    @Override
    public NotificationType type() {
        return NotificationType.DISABLED;
    }

    @Override
    public String format() {
        return StringUtil.GRAY_COLOR + this.type().name();
    }
}
