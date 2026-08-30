package com.github.imdmk.automessage.scheduled.placeholder;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.BiFunction;

/**
 * Values AutoMessage can substitute into a message without any other plugin installed.
 *
 * <p>
 * Each constant carries the token exactly as it is written in the configuration, braces included,
 * because that is the form Multification matches on.
 * </p>
 *
 * <p>
 * Only values the Spigot API can answer for belong here. TPS, for one, does not: it is a Paper
 * extension with no equivalent in the API this plugin compiles against, and reaching it would mean
 * runtime reflection that quietly fails on the servers the plugin claims to support. Servers that
 * want it can write {@code %server_tps%} and let PlaceholderAPI answer.
 * </p>
 *
 * <p>
 * Each constant also declares whether it needs a viewer. That distinction is what lets a
 * destination with no single reader - a Discord channel, say - still resolve the half of them that
 * describe the server rather than the person reading.
 * </p>
 */
public enum BuiltinPlaceholder {

    PLAYER("{PLAYER}", Scope.VIEWER, (server, player) -> player.getName()),
    DISPLAY_NAME("{DISPLAY_NAME}", Scope.VIEWER, (server, player) -> player.getDisplayName()),
    UUID("{UUID}", Scope.VIEWER, (server, player) -> player.getUniqueId().toString()),
    WORLD("{WORLD}", Scope.VIEWER, (server, player) -> player.getWorld().getName()),

    ONLINE("{ONLINE}", Scope.SERVER, (server, player) -> Integer.toString(server.getOnlinePlayers().size())),
    MAX_PLAYERS("{MAX_PLAYERS}", Scope.SERVER, (server, player) -> Integer.toString(server.getMaxPlayers())),

    DATE("{DATE}", Scope.SERVER, (server, player) -> Formats.now(Formats.DATE)),
    TIME("{TIME}", Scope.SERVER, (server, player) -> Formats.now(Formats.TIME));

    /** Whether a value describes the server or the person reading it. */
    public enum Scope {
        SERVER,
        VIEWER
    }

    private final String token;
    private final Scope scope;
    private final BiFunction<Server, Player, String> resolver;

    BuiltinPlaceholder(String token, Scope scope, BiFunction<Server, Player, String> resolver) {
        this.token = token;
        this.scope = scope;
        this.resolver = resolver;
    }

    public String token() {
        return token;
    }

    public Scope scope() {
        return scope;
    }

    public boolean requiresViewer() {
        return scope == Scope.VIEWER;
    }

    public String resolve(Server server, Player player) {
        return resolver.apply(server, player);
    }

    /**
     * Resolves a value that does not depend on who is reading.
     *
     * @throws IllegalStateException when called for a placeholder that needs a viewer
     */
    public String resolveForServer(Server server) {
        if (requiresViewer()) {
            throw new IllegalStateException(token + " cannot be resolved without a viewer");
        }

        return resolver.apply(server, null);
    }

    /**
     * Enum constants are initialised before the enum's own static fields, so the formatters live
     * here rather than beside the constants that use them.
     */
    private static final class Formats {

        private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ROOT);
        private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT);

        private static String now(DateTimeFormatter formatter) {
            return formatter.format(LocalDateTime.now());
        }
    }
}
