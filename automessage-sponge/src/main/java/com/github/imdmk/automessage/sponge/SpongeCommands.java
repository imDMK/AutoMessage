package com.github.imdmk.automessage.sponge;

import dev.rollczi.litecommands.LiteCommandsBaseBuilder;
import dev.rollczi.litecommands.LiteCommandsBuilder;
import dev.rollczi.litecommands.LiteCommandsFactory;
import dev.rollczi.litecommands.permission.PermissionResolver;
import dev.rollczi.litecommands.sponge.LiteSpongeSettings;
import dev.rollczi.litecommands.sponge.SpongePlatform;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.plugin.PluginContainer;

// What LiteSpongeFactory does, minus the one thing that cannot be done yet.
//
// The factory upstream builds its scheduler eagerly, and that scheduler asks Sponge for the main
// thread in its constructor - which throws when no engine is running. Sponge fires
// RegisterCommandEvent, the only moment it accepts commands, with no engine at all, so calling the
// factory in time to register anything is impossible. Everything else it sets up is either
// something this plugin does not use or something assembled here from public API.
final class SpongeCommands {

    private SpongeCommands() {
    }

    // The type parameter is the builder's own, exactly as LiteSpongeFactory declares it, so the
    // caller infers it and never has to name it.
    static <B extends LiteCommandsBaseBuilder<CommandCause, LiteSpongeSettings, B>>
    LiteCommandsBuilder<CommandCause, LiteSpongeSettings, B> builder(
            PluginContainer container,
            Game game
    ) {
        final LiteSpongeSettings settings = new LiteSpongeSettings();

        // The platform is what registers the listener that answers RegisterCommandEvent, so
        // constructing this is the whole point of doing it early.
        return LiteCommandsFactory.<CommandCause, LiteSpongeSettings, B>builder(
                        CommandCause.class,
                        internal -> new SpongePlatform(container, settings, internal.getPermissionService())
                )
                .permissionResolver(PermissionResolver.createDefault(
                        CommandCause.class, CommandCause::hasPermission
                ))
                .scheduler(new SpongeCommandScheduler(container, game));
    }
}
