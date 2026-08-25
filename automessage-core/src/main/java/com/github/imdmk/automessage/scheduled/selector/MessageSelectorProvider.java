package com.github.imdmk.automessage.scheduled.selector;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Supplies the {@link MessageSelector} matching the currently configured
 * {@link MessageSelectorType}.
 *
 * <p>
 * Selectors are stateful — {@link SequentialMessageSelector} remembers the position it stopped at.
 * The instance is therefore cached and only rebuilt when the configured strategy actually changes,
 * so reloading the configuration does not reset the rotation.
 * </p>
 */
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
