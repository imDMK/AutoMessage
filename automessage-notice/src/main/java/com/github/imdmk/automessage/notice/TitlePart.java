package com.github.imdmk.automessage.notice;

import java.util.Objects;

public record TitlePart(String text) implements NoticePart {

    public static final String KEY = "title";

    public TitlePart {
        Objects.requireNonNull(text, "text");
    }

    @Override
    public String key() {
        return KEY;
    }
}
