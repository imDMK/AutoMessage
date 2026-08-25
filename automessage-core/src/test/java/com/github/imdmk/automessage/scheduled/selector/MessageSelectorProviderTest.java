package com.github.imdmk.automessage.scheduled.selector;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

class MessageSelectorProviderTest {

    @Test
    @DisplayName("Should reuse the selector so its rotation state survives a config reload")
    void shouldReuseSelectorForSameType() {
        AtomicReference<MessageSelectorType> type = new AtomicReference<>(MessageSelectorType.SEQUENTIAL);
        MessageSelectorProvider provider = new MessageSelectorProvider(type::get);

        assertSame(provider.get(), provider.get());
    }

    @Test
    @DisplayName("Should build a new selector once the configured strategy changes")
    void shouldRebuildSelectorOnTypeChange() {
        AtomicReference<MessageSelectorType> type = new AtomicReference<>(MessageSelectorType.SEQUENTIAL);
        MessageSelectorProvider provider = new MessageSelectorProvider(type::get);

        MessageSelector sequential = provider.get();
        type.set(MessageSelectorType.RANDOM);
        MessageSelector random = provider.get();

        assertNotSame(sequential, random);
        assertNotSame(sequential.getClass(), random.getClass());
    }

    @Test
    @DisplayName("Should fall back to SEQUENTIAL when the strategy is missing")
    void shouldFallBackOnMissingType() {
        MessageSelectorProvider provider = new MessageSelectorProvider(() -> null);

        assertInstanceOf(MessageSelector.class, provider.get());
        assertSame(provider.get(), provider.get());
    }
}
