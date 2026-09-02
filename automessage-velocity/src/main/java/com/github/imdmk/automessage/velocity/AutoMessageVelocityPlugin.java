package com.github.imdmk.automessage.velocity;

import com.github.imdmk.automessage.AutoMessage;
import com.github.imdmk.automessage.command.CommandRegistrar;
import com.github.imdmk.automessage.logging.Slf4jPluginLogger;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolver;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.velocity.LiteVelocityFactory;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "automessage",
        name = AutoMessage.NAME,
        version = BuildConstants.VERSION,
        description = "High-performance plugin for fully customizable automatic server-wide broadcasts.",
        url = "https://github.com/imDMK/AutoMessage",
        authors = {"imDMK"}
)
public final class AutoMessageVelocityPlugin {

    private static final int BSTATS_PLUGIN_ID = 20909;

    private final ProxyServer proxy;
    private final Logger slf4jLogger;
    private final Path dataDirectory;
    private final Metrics.Factory metricsFactory;

    private AutoMessage automessage;
    private LiteCommands<?> liteCommands;

    @Inject
    public AutoMessageVelocityPlugin(
            ProxyServer proxy,
            Logger slf4jLogger,
            @DataDirectory Path dataDirectory,
            Metrics.Factory metricsFactory
    ) {
        this.proxy = proxy;
        this.slf4jLogger = slf4jLogger;
        this.dataDirectory = dataDirectory;
        this.metricsFactory = metricsFactory;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        final PluginLogger logger = new Slf4jPluginLogger(slf4jLogger);
        final TaskScheduler scheduler = new VelocityTaskScheduler(this, proxy);

        this.automessage = new AutoMessage(
                new VelocityPlatform(proxy, scheduler),
                logger,
                dataDirectory.toFile(),
                // PlaceholderAPI is a Bukkit plugin; nothing on a proxy owns %tokens%.
                ExternalPlaceholderResolver.disabled()
        );

        final VelocityViewerFactory viewers = new VelocityViewerFactory();

        proxy.getEventManager().register(
                this,
                new VelocityTriggerListener(proxy, viewers, automessage.triggerService())
        );

        final var commandBuilder = LiteVelocityFactory.builder(proxy);
        CommandRegistrar.configure(commandBuilder, automessage, viewers);

        this.liteCommands = commandBuilder.build();

        metricsFactory.make(this, BSTATS_PLUGIN_ID);
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (automessage == null) {
            return;
        }

        automessage.shutdown();
        liteCommands.unregister();

        this.automessage = null;
    }
}
