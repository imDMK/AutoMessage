package com.github.imdmk.automessage.notification;

public interface Notification {

    String message();

    NotificationType type();

    String format();
}
