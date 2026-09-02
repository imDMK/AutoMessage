package com.github.imdmk.automessage.notice;

import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public record ChatPart(@Unmodifiable List<String> lines) implements NoticePart {

    public ChatPart {
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("a chat notice needs at least one line");
        }

        lines = List.copyOf(lines);
    }

    public static ChatPart of(String... lines) {
        return new ChatPart(List.of(lines));
    }

    @Override
    public String key() {
        return null;
    }
}
