package com.github.imdmk.automessage.scheduled.selector;

import java.util.Objects;
import java.util.function.Supplier;

public final class MessageSelectorProvider implements Supplier<MessageSelector> {

    private final Supplier<MessageSelectorType> typeSupplier;

    private MessageSelectorType currentType;
    private MessageSelector currentSelector;

    public MessageSelectorProvider(Supplier<MessageSelectorType> typeSupplier) {
        this.typeSupplier = typeSupplier;
    }

    @Override
    public synchronized MessageSelector get() {
        final MessageSelectorType type = Objects.requireNonNullElse(typeSupplier.get(), MessageSelectorType.SEQUENTIAL);

        if (currentSelector == null || currentType != type) {
            currentType = type;
            currentSelector = MessageSelectorFactory.create(type);
        }

        return currentSelector;
    }
}
