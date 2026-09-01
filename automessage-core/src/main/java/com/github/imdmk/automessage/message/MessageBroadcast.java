package com.github.imdmk.automessage.message;

import com.github.imdmk.automessage.language.LanguageConfig;
import com.github.imdmk.automessage.notice.Notice;
import com.github.imdmk.automessage.platform.viewer.Viewer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class MessageBroadcast {

    private final MessageService service;
    private final Map<String, String> placeholders = new LinkedHashMap<>();

    private Viewer viewer;
    private Function<LanguageConfig, Notice> message;

    MessageBroadcast(MessageService service) {
        this.service = service;
    }

    public MessageBroadcast viewer(Viewer viewer) {
        this.viewer = viewer;
        return this;
    }

    public MessageBroadcast notice(Function<LanguageConfig, Notice> message) {
        this.message = message;
        return this;
    }

    public MessageBroadcast notice(Notice notice) {
        this.message = language -> notice;
        return this;
    }

    public MessageBroadcast placeholder(String key, String value) {
        this.placeholders.put(key, value);
        return this;
    }

    public void send() {
        Objects.requireNonNull(viewer, "viewer");
        Objects.requireNonNull(message, "message");

        final Notice notice = message.apply(service.languageOf(viewer));

        if (notice == null) {
            return;
        }

        service.render(viewer, notice, this::substitute);
    }

    private String substitute(String text) {
        if (placeholders.isEmpty()) {
            return text;
        }

        String substituted = text;

        for (final Map.Entry<String, String> placeholder : placeholders.entrySet()) {
            substituted = substituted.replace(placeholder.getKey(), placeholder.getValue());
        }

        return substituted;
    }
}
