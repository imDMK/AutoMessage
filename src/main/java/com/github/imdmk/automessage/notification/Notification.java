package com.github.imdmk.automessage.notification;

public interface Notification {

    NotificationType type();

    String format();

    String formatHover();
}
