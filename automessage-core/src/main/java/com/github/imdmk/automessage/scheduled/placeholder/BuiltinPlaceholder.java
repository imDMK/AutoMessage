package com.github.imdmk.automessage.scheduled.placeholder;

import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.BiFunction;

public enum BuiltinPlaceholder {

    PLAYER("{PLAYER}", Scope.VIEWER, (viewers, viewer) -> viewer.name()),
    DISPLAY_NAME("{DISPLAY_NAME}", Scope.VIEWER, (viewers, viewer) -> viewer.displayName()),
    UUID("{UUID}", Scope.VIEWER, (viewers, viewer) -> viewer.uniqueId().toString()),
    WORLD("{WORLD}", Scope.VIEWER, (viewers, viewer) -> viewer.world().orElse("")),

    ONLINE("{ONLINE}", Scope.SERVER, (viewers, viewer) -> Integer.toString(viewers.onlineCount())),
    MAX_PLAYERS("{MAX_PLAYERS}", Scope.SERVER, (viewers, viewer) -> Integer.toString(viewers.maxPlayers())),

    DATE("{DATE}", Scope.SERVER, (viewers, viewer) -> Formats.now(Formats.DATE)),
    TIME("{TIME}", Scope.SERVER, (viewers, viewer) -> Formats.now(Formats.TIME));

    private enum Scope {
        SERVER,
        VIEWER
    }

    // The token carries its braces because that is exactly what is matched in the text.
    private final String token;
    private final Scope scope;
    private final BiFunction<ViewerRegistry, Viewer, String> resolver;

    BuiltinPlaceholder(String token, Scope scope, BiFunction<ViewerRegistry, Viewer, String> resolver) {
        this.token = token;
        this.scope = scope;
        this.resolver = resolver;
    }

    public String token() {
        return token;
    }

    public boolean requiresViewer() {
        return scope == Scope.VIEWER;
    }

    public String resolve(ViewerRegistry viewers, Viewer viewer) {
        return resolver.apply(viewers, viewer);
    }

    public String resolveForServer(ViewerRegistry viewers) {
        if (requiresViewer()) {
            throw new IllegalStateException(token + " cannot be resolved without a viewer");
        }

        return resolver.apply(viewers, null);
    }

    private static final class Formats {

        private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ROOT);
        private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT);

        private static String now(DateTimeFormatter formatter) {
            return formatter.format(LocalDateTime.now());
        }
    }
}
