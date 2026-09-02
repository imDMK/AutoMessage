package com.github.imdmk.automessage.minestom;

import com.github.imdmk.automessage.platform.viewer.Viewer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public final class MinestomViewer implements Viewer {

    private static final String CONSOLE_NAME = "CONSOLE";

    private final CommandSender sender;
    private final MinestomPermissions permissions;

    public MinestomViewer(CommandSender sender, MinestomPermissions permissions) {
        this.sender = sender;
        this.permissions = permissions;
    }

    @Override
    public String name() {
        return sender instanceof Player player ? player.getUsername() : CONSOLE_NAME;
    }

    @Override
    public UUID uniqueId() {
        return sender instanceof Player player ? player.getUuid() : CONSOLE_ID;
    }

    @Override
    public String displayName() {
        if (!(sender instanceof Player player)) {
            return CONSOLE_NAME;
        }

        // Null until something sets one - Minestom decorates nobody by itself.
        final Component displayName = player.getDisplayName();

        return displayName != null
                ? PlainTextComponentSerializer.plainText().serialize(displayName)
                : player.getUsername();
    }

    @Override
    public String locale() {
        if (!(sender instanceof Player player)) {
            return "";
        }

        // Lowercased with an underscore, matching the raw Bukkit client string, so one language
        // file name works whichever platform reads it - see Viewer#locale. Null until the client
        // has sent its settings.
        return Optional.ofNullable(player.getLocale())
                .map(locale -> locale.toString().toLowerCase(Locale.ROOT))
                .orElse("");
    }

    @Override
    public Optional<String> world() {
        return Optional.empty();
    }

    @Override
    public boolean hasPermission(String permission) {
        return permissions.has(sender, permission);
    }

    @Override
    public boolean isPlayer() {
        return sender instanceof Player;
    }

    @Override
    public boolean isOnline() {
        return !(sender instanceof Player player) || player.isOnline();
    }

    @Override
    public Audience audience() {
        // Minestom speaks Adventure natively: a CommandSender already is an Audience.
        return sender;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof MinestomViewer viewer && uniqueId().equals(viewer.uniqueId());
    }

    @Override
    public int hashCode() {
        return uniqueId().hashCode();
    }
}
