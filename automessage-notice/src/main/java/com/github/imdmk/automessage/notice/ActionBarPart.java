package com.github.imdmk.automessage.notice;

import java.util.Objects;

public record ActionBarPart(String text) implements NoticePart {

    public static final String KEY = "actionbar";

    public ActionBarPart {
        Objects.requireNonNull(text, "text");
    }

    @Override
    public String key() {
        return KEY;
    }
}
