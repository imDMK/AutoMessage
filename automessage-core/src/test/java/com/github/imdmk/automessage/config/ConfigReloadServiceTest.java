package com.github.imdmk.automessage.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ConfigReloadServiceTest {

    private final ConfigManager configManager = mock(ConfigManager.class);
    private final ConfigReloadService service = new ConfigReloadService(configManager);

    @Test
    @DisplayName("reload(): should load configs and notify every listener in order")
    void reload_shouldLoadAndNotifyListeners() {
        List<String> notified = new ArrayList<>();

        service.register(() -> notified.add("first"));
        service.register(() -> notified.add("second"));

        service.reload();

        verify(configManager).loadAll();
        assertEquals(List.of("first", "second"), notified);
    }

    @Test
    @DisplayName("reload(): should not notify listeners when loading fails")
    void reload_shouldNotNotifyOnFailure() {
        List<String> notified = new ArrayList<>();
        service.register(() -> notified.add("called"));

        doThrow(new ConfigAccessException("broken")).when(configManager).loadAll();

        assertThrows(ConfigAccessException.class, service::reload);
        assertTrue(notified.isEmpty());
    }
}
