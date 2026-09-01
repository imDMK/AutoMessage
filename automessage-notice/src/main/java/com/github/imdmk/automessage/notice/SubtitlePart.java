package com.github.imdmk.automessage.notice;

import java.util.Objects;

public record SubtitlePart(String text) implements NoticePart {

    public static final String KEY = "subtitle";

    public SubtitlePart {
        Objects.requireNonNull(text, "text");
    }

    @Override
    public String key() {
        return KEY;
    }
}
