package com.github.imdmk.automessage.bukkit;

import com.github.imdmk.automessage.platform.viewer.Viewer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public final class BukkitViewer implements Viewer {

    private static final String CONSOLE_NAME = "CONSOLE";

    private final CommandSender sender;
    private final AudienceProvider audiences;

    public BukkitViewer(CommandSender sender, AudienceProvider audiences) {
        this.sender = sender;
        this.audiences = audiences;
    }

    public CommandSender sender() {
        return sender;
    }

    @Override
    public String name() {
        return sender instanceof Player player ? player.getName() : CONSOLE_NAME;
    }

    @Override
    public UUID uniqueId() {
        return sender instanceof Player player ? player.getUniqueId() : CONSOLE_ID;
    }

    @Override
    public String displayName() {
        return sender instanceof Player player ? player.getDisplayName() : CONSOLE_NAME;
    }

    @Override
    public String locale() {
        // The raw client string, never wrapped in a Locale - see Viewer#locale.
        return sender instanceof Player player ? player.getLocale() : "";
    }

    @Override
    public Optional<String> world() {
        return sender instanceof Player player
                ? Optional.of(player.getWorld().getName())
                : Optional.empty();
    }

    @Override
    public boolean isPlayer() {
        return sender instanceof Player;
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean isOnline() {
        return !(sender instanceof Player player) || player.isOnline();
    }

    @Override
    public Audience audience() {
        return sender instanceof Player player
                ? audiences.player(player.getUniqueId())
                : audiences.console();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof BukkitViewer viewer && uniqueId().equals(viewer.uniqueId());
    }

    @Override
    public int hashCode() {
        return uniqueId().hashCode();
    }
}
