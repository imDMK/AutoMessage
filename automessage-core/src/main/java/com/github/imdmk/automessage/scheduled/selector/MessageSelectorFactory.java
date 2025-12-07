package com.github.imdmk.automessage.scheduled.selector;

import org.jetbrains.annotations.NotNull;

public final class MessageSelectorFactory {

    private MessageSelectorFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static @NotNull MessageSelector create(@NotNull MessageSelectorType type) {
        return switch (type) {
            case RANDOM -> new RandomMessageSelector();
            case SEQUENTIAL -> new SequentialMessageSelector();
        };
    }
}

