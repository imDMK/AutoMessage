package com.github.imdmk.automessage.fabric;

import com.github.imdmk.automessage.platform.viewer.Viewer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.command.permission.Permission;
import net.minecraft.command.permission.PermissionLevel;
import net.minecraft.command.permission.PermissionPredicate;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public final class FabricViewer implements Viewer {

    private static final String CONSOLE_NAME = "CONSOLE";

    private static final PermissionLevel OPERATOR = PermissionLevel.GAMEMASTERS;

    private final MinecraftServerAudiences audiences;
    private final PermissionPredicate permissions;
    private final @Nullable ServerPlayerEntity player;
    private final Audience audience;

    private FabricViewer(
            MinecraftServerAudiences audiences,
            PermissionPredicate permissions,
            @Nullable ServerPlayerEntity player,
            Audience audience
    ) {
        this.audiences = audiences;
        this.permissions = permissions;
        this.player = player;
        this.audience = audience;
    }

    public static FabricViewer of(MinecraftServerAudiences audiences, ServerPlayerEntity player) {
        return new FabricViewer(
                audiences,
                player.getPermissions(),
                player,
                audiences.audience(player)
        );
    }

    public static FabricViewer of(MinecraftServerAudiences audiences, ServerCommandSource source) {
        final ServerPlayerEntity player = source.getPlayer();

        return player != null
                ? of(audiences, player)
                : new FabricViewer(audiences, source.getPermissions(), null, audiences.audience(source));
    }

    public Optional<ServerPlayerEntity> player() {
        return Optional.ofNullable(player);
    }

    @Override
    public String name() {
        return player != null ? player.getGameProfile().name() : CONSOLE_NAME;
    }

    @Override
    public UUID uniqueId() {
        return player != null ? player.getUuid() : CONSOLE_ID;
    }

    @Override
    public String displayName() {
        // Minecraft's display name is a Text; flattened because the rest of the plugin treats a
        // display name as something to substitute into a message, not as markup to nest.
        return player != null ? player.getDisplayName().getString() : CONSOLE_NAME;
    }

    @Override
    public String locale() {
        // Lowercased, matching the raw Bukkit client string, so one language file name works
        // whichever platform reads it - see Viewer#locale.
        return player != null
                ? player.getClientOptions().language().toLowerCase(Locale.ROOT)
                : "";
    }

    @Override
    public Optional<String> world() {
        return player != null
                ? Optional.of(player.getEntityWorld().getRegistryKey().getValue().toString())
                : Optional.empty();
    }

    @Override
    public boolean hasPermission(String permission) {
        final Identifier identifier = Identifier.trySplitOn(permission.toLowerCase(Locale.ROOT), '.');

        if (identifier != null && permissions.hasPermission(Permission.Atom.of(identifier))) {
            return true;
        }

        return permissions.hasPermission(new Permission.Level(OPERATOR));
    }

    @Override
    public boolean isPlayer() {
        return player != null;
    }

    @Override
    public boolean isOnline() {
        return player == null || !player.isDisconnected();
    }

    @Override
    public Audience audience() {
        return audience;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof FabricViewer viewer && uniqueId().equals(viewer.uniqueId());
    }

    @Override
    public int hashCode() {
        return uniqueId().hashCode();
    }
}
