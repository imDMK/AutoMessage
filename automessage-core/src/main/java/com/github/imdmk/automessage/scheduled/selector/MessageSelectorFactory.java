package com.github.imdmk.automessage.scheduled.selector;

public final class MessageSelectorFactory {

    private MessageSelectorFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static MessageSelector create(MessageSelectorType type) {
        return switch (type) {
            case RANDOM -> new RandomMessageSelector();
            case SHUFFLE -> new ShuffleMessageSelector();
            case SEQUENTIAL -> new SequentialMessageSelector();
            case WEIGHTED -> new WeightedMessageSelector();
        };
    }
}

