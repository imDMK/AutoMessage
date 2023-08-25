package com.github.imdmk.automessage.notification.implementation;

import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationType;
import com.github.imdmk.automessage.util.ComponentUtil;
import com.github.imdmk.automessage.util.StringUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

public record TitleNotification(String title, String subtitle, Title.Times times) implements Notification {

    public TitleNotification(String title, String subtitle) {
        this(title, subtitle, Title.DEFAULT_TIMES);
    }

    public Title create() {
        Component title = ComponentUtil.deserialize(this.title);
        Component subtitle = ComponentUtil.deserialize(this.subtitle);

        return Title.title(title, subtitle, this.times);
    }

    @Override
    public NotificationType type() {
        return NotificationType.TITLE;
    }

    @Override
    public String format() {
        return StringUtil.GRAY_COLOR + this.type().name() + ":"
                + StringUtil.NEW_LINE + StringUtil.GRAY_COLOR + "title: " + this.title
                + StringUtil.NEW_LINE + StringUtil.GRAY_COLOR + "subtitle: " + this.subtitle;
    }
}
