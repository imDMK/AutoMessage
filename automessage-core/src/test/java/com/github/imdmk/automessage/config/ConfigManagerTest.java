package com.github.imdmk.automessage.config;

import com.github.imdmk.automessage.platform.logger.PluginLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertSame;
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
        SampleConfig config = new SampleConfig();
        when(factory.create(SampleConfig.class)).thenReturn(config);

        SampleConfig result = manager.create(SampleConfig.class);

        assertSame(config, result);

        verify(factory).create(SampleConfig.class);
        verify(binder).bind(eq(config), any(File.class));
        verify(lifecycle).initialize(config);

    }

    @Test
    @DisplayName("loadAll(): should delegate loading to lifecycle")
    void loadAll_shouldDelegateToLifecycle() {
        SampleConfig config = new SampleConfig();
        when(factory.create(SampleConfig.class)).thenReturn(config);

        manager.create(SampleConfig.class);
        manager.loadAll();

        verify(lifecycle).load(config);
    }

    @Test
    @DisplayName("saveAll(): should delegate saving to lifecycle")
    void saveAll_shouldDelegateToLifecycle() {
        SampleConfig config = new SampleConfig();
        when(factory.create(SampleConfig.class)).thenReturn(config);

        manager.create(SampleConfig.class);
        manager.saveAll();

        verify(lifecycle).save(config);
    }

    @Test
    @DisplayName("clearAll(): should remove all configs from registry")
    void clearAll_shouldRemoveAllConfigs() {
        when(factory.create(SampleConfig.class)).thenReturn(new SampleConfig());

        manager.create(SampleConfig.class);
        manager.clearAll();

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
