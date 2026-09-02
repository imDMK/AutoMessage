package com.github.imdmk.automessage.velocity;

import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.audience.Audience;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public final class VelocityViewer implements Viewer {

    private static final String CONSOLE_NAME = "CONSOLE";

    private final CommandSource source;

    public VelocityViewer(CommandSource source) {
        this.source = source;
    }

    @Override
    public String name() {
        return source instanceof Player player ? player.getUsername() : CONSOLE_NAME;
    }

    @Override
    public UUID uniqueId() {
        return source instanceof Player player ? player.getUniqueId() : CONSOLE_ID;
    }

    @Override
    public String displayName() {
        return name();
    }

    @Override
    public String locale() {
        if (!(source instanceof Player player)) {
            return "";
        }

        // Lowercased with an underscore, matching what the Bukkit client string looks like, so
        // one language file name works whichever platform reads it - see Viewer#locale.
        // Null until the client has sent its settings, which it has not yet done for a player
        // caught in the moment right after login.
        return Optional.ofNullable(player.getEffectiveLocale())
                .map(locale -> locale.toString().toLowerCase(Locale.ROOT))
                .orElse("");
    }

    @Override
    public Optional<String> world() {
        return Optional.empty();
    }

    @Override
    public boolean hasPermission(String permission) {
        return source.hasPermission(permission);
    }

    @Override
    public boolean isPlayer() {
        return source instanceof Player;
    }

    @Override
    public boolean isOnline() {
        return !(source instanceof Player player) || player.isActive();
    }

    @Override
    public Audience audience() {
        // Velocity speaks Adventure natively, so there is nothing to adapt.
        return source;
    }
}
