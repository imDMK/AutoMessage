package com.github.imdmk.automessage.scheduled.selector;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageSelectorFactoryTest {

    @Test
    @DisplayName("Factory should create a RandomMessageSelector when RANDOM is chosen")
    void shouldCreateRandomSelector() {
        MessageSelector selector = MessageSelectorFactory.create(MessageSelectorType.RANDOM);

        assertNotNull(selector);
        assertEquals(RandomMessageSelector.class, selector.getClass());
    }

    @Test
    @DisplayName("Factory should create a SequentialMessageSelector when SEQUENTIAL is chosen")
    void shouldCreateSequentialSelector() {
        MessageSelector selector = MessageSelectorFactory.create(MessageSelectorType.SEQUENTIAL);

        assertNotNull(selector);
        assertEquals(SequentialMessageSelector.class, selector.getClass());
    }

    @Test
    @DisplayName("Factory should reject null type")
    void shouldRejectNullType() {
        assertThrows(NullPointerException.class, () ->
                MessageSelectorFactory.create(null)
        );
    }
}

