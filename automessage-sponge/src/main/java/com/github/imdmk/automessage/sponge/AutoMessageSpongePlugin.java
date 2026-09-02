package com.github.imdmk.automessage.sponge;

import com.github.imdmk.automessage.AutoMessage;
import com.github.imdmk.automessage.command.CommandRegistrar;
import com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolver;
import com.google.inject.Inject;
import dev.rollczi.litecommands.LiteCommands;
import org.bstats.sponge.Metrics;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.lang.invoke.MethodHandles;
import java.nio.file.Path;

@Plugin("automessage")
public final class AutoMessageSpongePlugin {

    private static final int BSTATS_PLUGIN_ID = 20910;

    private final PluginContainer container;
    private final Game game;
    private final Path configDirectory;
    private final Metrics metrics;

    private AutoMessage automessage;
    private SpongeTaskScheduler scheduler;
    private LiteCommands<?> liteCommands;

    @Inject
    public AutoMessageSpongePlugin(
            PluginContainer container,
            Game game,
            @ConfigDir(sharedRoot = false) Path configDirectory,
            Metrics.Factory metricsFactory
    ) {
        this.container = container;
        this.game = game;
        this.configDirectory = configDirectory;
        this.metrics = metricsFactory.make(BSTATS_PLUGIN_ID);
    }

    // Everything is built here, before Sponge asks plugins to register commands - an event whose
    // own documentation warns it "does not guarantee that any specific engine is running", and on
    // SpongeVanilla none is. The scheduler and the viewer registry are both written to survive
    // that, and the command binding is assembled by SpongeCommands for the same reason.
    @Listener
    public void onConstruct(ConstructPluginEvent event) {
        final SpongeTaskScheduler scheduler = new SpongeTaskScheduler(container, game);
        this.scheduler = scheduler;

        this.automessage = new AutoMessage(
                new SpongePlatform(game, scheduler),
                new SpongePluginLogger(container.logger()),
                configDirectory.toFile(),
                // PlaceholderAPI is a Bukkit plugin and has no Sponge build; nothing here owns
                // another plugin's %tokens%.
                ExternalPlaceholderResolver.disabled()
        );

        // The lookup overload, not the two-argument one: without it Sponge reflects into the
        // listener itself, which is deprecated and fails outright on a module it cannot open.
        game.eventManager().registerListeners(
                container,
                new SpongeTriggerListener(game, automessage.triggerService()),
                MethodHandles.lookup()
        );

        final var commandBuilder = SpongeCommands.builder(container, game);
        CommandRegistrar.configure(commandBuilder, automessage, new SpongeViewerFactory());

        this.liteCommands = commandBuilder.build();

        metrics.startup(event);
    }

    // Hands the scheduler the server thread it has been waiting for; anything scheduled during
    // construction, the broadcast loop included, has been held until now.
    @Listener
    public void onStarted(StartedEngineEvent<Server> event) {
        scheduler.engineStarted();
    }

    // Each part is checked on its own: startup can fail partway, and a teardown that assumes it
    // did not throws over the top of the failure that actually matters.
    @Listener
    public void onStopping(StoppingEngineEvent<Server> event) {
        if (automessage != null) {
            automessage.shutdown();
            this.automessage = null;
        }

        if (liteCommands != null) {
            liteCommands.unregister();
            this.liteCommands = null;
        }

        metrics.shutdown();
    }
}
