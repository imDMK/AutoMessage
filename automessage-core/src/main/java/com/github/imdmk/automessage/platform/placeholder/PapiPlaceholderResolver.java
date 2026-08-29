package com.github.imdmk.automessage.platform.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

/**
 * Bridges to PlaceholderAPI.
 *
 * <p>
 * PlaceholderAPI is a soft dependency: this class is only ever loaded once
 * {@link ExternalPlaceholderResolverFactory} has seen the plugin enabled, so a server without it
 * never touches the {@code me.clip} classes and never sees a NoClassDefFoundError.
 * </p>
 */
final class PapiPlaceholderResolver implements ExternalPlaceholderResolver {

    @Override
    public String resolve(Player viewer, String token) {
        return PlaceholderAPI.setPlaceholders(viewer, token);
    }

    @Override
    public boolean available() {
        return true;
    }
}
