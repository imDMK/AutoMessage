package com.github.imdmk.automessage.config;

import com.github.imdmk.automessage.platform.logger.PluginLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConfigManagerTest {

    private ConfigManager manager;

    private ConfigFactory factory;
    private ConfigBinder binder;
    private ConfigLifecycle lifecycle;

    private final PluginLogger logger = mock(PluginLogger.class);
    private final File dataFolder = new File("build/test-configs");

    @BeforeEach
    @DisplayName("Setup test environment and inject mock dependencies")
    void setUp() {
        manager = new ConfigManager(logger, dataFolder);

        factory = mock(ConfigFactory.class);
        binder = mock(ConfigBinder.class);
        lifecycle = mock(ConfigLifecycle.class);

        inject("factory", factory);
        inject("binder", binder);
        inject("lifecycle", lifecycle);
    }

    @Test
    @DisplayName("create(): should instantiate, bind, initialize and register config")
    void create_shouldInstantiateBindInitializeAndRegister() {
        // given
        SampleConfig config = new SampleConfig();
        when(factory.instantiate(SampleConfig.class)).thenReturn(config);

        // when
        SampleConfig result = manager.create(SampleConfig.class);

        // then
        assertSame(config, result);

        verify(factory).instantiate(SampleConfig.class);
        verify(binder).bind(eq(config), any(File.class));
        verify(lifecycle).initialize(config);

        assertTrue(manager.getConfigs().contains(config));
        assertSame(config, manager.get(SampleConfig.class));
    }

    @Test
    @DisplayName("createAll(): should call create() for each provided config class")
    void createAll_shouldCreateEachConfig() {
        when(factory.instantiate(SampleConfig.class)).thenReturn(new SampleConfig());

        manager.createAll(List.of(SampleConfig.class));

        verify(factory).instantiate(SampleConfig.class);
    }

    @Test
    @DisplayName("require(): should throw when config has not been created earlier")
    void require_shouldThrowWhenNotCreated() {
        assertThrows(IllegalStateException.class, () -> manager.require(SampleConfig.class));
    }

    @Test
    @DisplayName("loadAll(): should delegate loading to lifecycle")
    void loadAll_shouldDelegateToLifecycle() {
        SampleConfig config = new SampleConfig();
        when(factory.instantiate(SampleConfig.class)).thenReturn(config);

        manager.create(SampleConfig.class);
        manager.loadAll();

        verify(lifecycle).load(config);
    }

    @Test
    @DisplayName("saveAll(): should delegate saving to lifecycle")
    void saveAll_shouldDelegateToLifecycle() {
        SampleConfig config = new SampleConfig();
        when(factory.instantiate(SampleConfig.class)).thenReturn(config);

        manager.create(SampleConfig.class);
        manager.saveAll();

        verify(lifecycle).save(config);
    }

    @Test
    @DisplayName("clearAll(): should remove all configs from registry")
    void clearAll_shouldRemoveAllConfigs() {
        when(factory.instantiate(SampleConfig.class)).thenReturn(new SampleConfig());

        manager.create(SampleConfig.class);
        manager.clearAll();

        assertTrue(manager.getConfigs().isEmpty());
        assertNull(manager.get(SampleConfig.class));
    }

    private void inject(String field, Object value) {
        try {
            var f = ConfigManager.class.getDeclaredField(field);
            f.setAccessible(true);
            f.set(manager, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
