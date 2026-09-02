package com.github.imdmk.automessage.platform.viewer;

import net.kyori.adventure.audience.Audience;

import java.util.Optional;
import java.util.UUID;

public interface Viewer {

    UUID CONSOLE_ID = new UUID(0L, 0L);

    String name();

    UUID uniqueId();

    String displayName();

    // A string, never a Locale: wrapping Bukkit's "pl_pl" in new Locale(...) puts the whole code
    // in the language field, which is what silently sent Polish players English text.
    String locale();

    Optional<String> world();

    boolean hasPermission(String permission);

    boolean isOnline();

    boolean isPlayer();

    Audience audience();
}
