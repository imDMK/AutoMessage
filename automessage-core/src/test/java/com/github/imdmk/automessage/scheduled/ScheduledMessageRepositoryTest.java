package com.github.imdmk.automessage.scheduled;

import com.eternalcode.multification.notice.Notice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduledMessageRepositoryTest {

    private ScheduledMessagesConfig config;
    private ScheduledMessageRepository repository;

    private static ScheduledMessage message(String name) {
        return new ScheduledMessage(name, List.of(Notice.chat("x")), List.of());
    }

    @BeforeEach
    void setUp() {
        config = new ScheduledMessagesConfig();
        config.messages = List.of(message("first"), message("second"));

        repository = ScheduledMessageRepository.config(config);
    }

    @Test
    @DisplayName("findAll(): should return every configured message in order")
    void findAll_shouldReturnConfiguredMessages() {
        assertEquals(List.of("first", "second"), repository.names());
    }

    @Test
    @DisplayName("findByName(): should find a message regardless of case")
    void findByName_shouldIgnoreCase() {
        assertEquals("first", repository.findByName("FiRsT").orElseThrow().name());
    }

    @Test
    @DisplayName("findByName(): should return empty for unknown, blank and null names")
    void findByName_shouldReturnEmptyForMissingNames() {
        assertTrue(repository.findByName("missing").isEmpty());
        assertTrue(repository.findByName("  ").isEmpty());
        assertTrue(repository.findByName(null).isEmpty());
    }

    @Test
    @DisplayName("Should observe messages added by a configuration reload")
    void shouldObserveReloadedMessages() {
        config.messages = List.of(message("third"));

        assertEquals(List.of("third"), repository.names());
        assertTrue(repository.findByName("first").isEmpty());
    }

    @Test
    @DisplayName("Should tolerate a configuration without any messages")
    void shouldTolerateMissingMessages() {
        config.messages = null;

        assertTrue(repository.findAll().isEmpty());
        assertTrue(repository.names().isEmpty());
        assertTrue(repository.findByName("first").isEmpty());
    }

    @Test
    @DisplayName("findAll(): should not expose the configuration list for modification")
    void findAll_shouldReturnImmutableCopy() {
        config.messages = new ArrayList<>(List.of(message("first")));

        assertThrows(UnsupportedOperationException.class, () -> repository.findAll().add(message("hack")));
    }
}
