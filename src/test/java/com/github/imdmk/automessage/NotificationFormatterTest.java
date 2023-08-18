package com.github.imdmk.automessage;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationFormatter;
import com.github.imdmk.automessage.notification.implementation.ChatNotification;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotificationFormatterTest {

    @Test
    void testFormat() {
        ChatNotification notificationToFormat = new ChatNotification("This plugin is called {PLUGIN} and its author is {AUTHOR}.");

        Notification excepted = new ChatNotification("This plugin is called AutoMessage and its author is imDMK.");
        Notification result = new NotificationFormatter(notificationToFormat)
                .placeholder("{PLUGIN}", "AutoMessage")
                .placeholder("{AUTHOR}", "imDMK")
                .build();

        assertEquals(excepted, result);
    }
}
