package com.github.imdmk.automessage.notice;

public record HideTitlePart() implements NoticePart {

    public static final String KEY = "hideTitle";

    @Override
    public String key() {
        return KEY;
    }
}
