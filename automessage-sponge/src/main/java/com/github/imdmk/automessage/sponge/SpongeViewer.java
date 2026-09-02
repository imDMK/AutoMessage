package com.github.imdmk.automessage.sponge;

import com.github.imdmk.automessage.platform.viewer.Viewer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.permission.Subject;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public final class SpongeViewer implements Viewer {

    private static final String CONSOLE_NAME = "CONSOLE";

    private final Audience audience;
    private final Subject subject;
    private final @Nullable ServerPlayer player;

    private SpongeViewer(Audience audience, Subject subject, @Nullable ServerPlayer player) {
        this.audience = audience;
        this.subject = subject;
        this.player = player;
    }

    public static SpongeViewer of(ServerPlayer player) {
        return new SpongeViewer(player, player, player);
    }

    public static SpongeViewer of(CommandCause cause) {
        return cause.first(ServerPlayer.class)
                .map(SpongeViewer::of)
                .orElseGet(() -> new SpongeViewer(cause.audience(), cause.subject(), null));
    }

    public Optional<ServerPlayer> player() {
        return Optional.ofNullable(player);
    }

    @Override
    public String name() {
        return player != null ? player.name() : CONSOLE_NAME;
    }

    @Override
    public UUID uniqueId() {
        return player != null ? player.uniqueId() : CONSOLE_ID;
    }

    @Override
    public String displayName() {
        if (player == null) {
            return CONSOLE_NAME;
        }

        // Sponge's display name is a Component; flattened here because the rest of the plugin
        // treats a display name as text to substitute into a message, not as markup to nest.
        return player.get(Keys.DISPLAY_NAME)
                .map(PlainTextComponentSerializer.plainText()::serialize)
                .orElseGet(player::name);
    }

    @Override
    public String locale() {
        // Lowercased with an underscore, matching the raw Bukkit client string, so one language
        // file name works whichever platform reads it - see Viewer#locale.
        return player != null
                ? player.locale().toString().toLowerCase(Locale.ROOT)
                : "";
    }

    @Override
    public Optional<String> world() {
        return player != null
                ? Optional.of(player.world().key().asString())
                : Optional.empty();
    }

    @Override
    public boolean hasPermission(String permission) {
        return subject.hasPermission(permission);
    }

    @Override
    public boolean isPlayer() {
        return player != null;
    }

    @Override
    public boolean isOnline() {
        return player == null || player.isOnline();
    }

    @Override
    public Audience audience() {
        // Sponge speaks Adventure natively, so there is nothing to adapt.
        return audience;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof SpongeViewer viewer && uniqueId().equals(viewer.uniqueId());
    }

    @Override
    public int hashCode() {
        return uniqueId().hashCode();
    }
}
