package com.github.imdmk.automessage.notification;

public interface Notification {

    NotificationType type();

    String message();

    String format();
}
