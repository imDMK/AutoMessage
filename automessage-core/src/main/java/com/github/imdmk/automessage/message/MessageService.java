package com.github.imdmk.automessage.message;

import com.github.imdmk.automessage.language.LanguageConfig;
import com.github.imdmk.automessage.language.LanguageRegistry;
import com.github.imdmk.automessage.notice.Notice;
import com.github.imdmk.automessage.notice.NoticeDelayer;
import com.github.imdmk.automessage.notice.NoticeRenderer;
import com.github.imdmk.automessage.platform.viewer.Viewer;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public final class MessageService {

    private final LanguageRegistry languages;
    private final NoticeRenderer renderer;

    public MessageService(LanguageRegistry languages, NoticeDelayer delayer) {
        this.languages = languages;
        this.renderer = NoticeRenderer.miniMessage(delayer);
    }

    public MessageBroadcast create() {
        return new MessageBroadcast(this);
    }

    public void send(Viewer viewer, Function<LanguageConfig, Notice> message) {
        create().viewer(viewer).notice(message).send();
    }

    void render(Viewer viewer, Notice notice, UnaryOperator<String> placeholders) {
        renderer.render(notice, viewer.audience(), placeholders);
    }

    LanguageConfig languageOf(Viewer viewer) {
        return languages.provide(viewer.locale());
    }
}
