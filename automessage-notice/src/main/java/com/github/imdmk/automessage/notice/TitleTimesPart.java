package com.github.imdmk.automessage.notice;

import java.time.Duration;
import java.util.Objects;

public record TitleTimesPart(Duration fadeIn, Duration stay, Duration fadeOut) implements NoticePart {

    public static final String KEY = "times";

    public TitleTimesPart {
        Objects.requireNonNull(fadeIn, "fadeIn");
        Objects.requireNonNull(stay, "stay");
        Objects.requireNonNull(fadeOut, "fadeOut");

        if (fadeIn.isNegative() || stay.isNegative() || fadeOut.isNegative()) {
            throw new IllegalArgumentException("title times must not be negative");
        }
    }

    @Override
    public String key() {
        return KEY;
    }
}
