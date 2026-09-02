package com.github.imdmk.automessage.bukkit.placeholder;

import com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolver;

import me.clip.placeholderapi.PlaceholderAPI;
import com.github.imdmk.automessage.bukkit.BukkitViewer;
import org.bukkit.entity.Player;
import com.github.imdmk.automessage.platform.viewer.Viewer;

final class PapiPlaceholderResolver implements ExternalPlaceholderResolver {

    @Override
    public String resolve(Viewer viewer, String token) {
        // PlaceholderAPI is a Bukkit plugin and wants a Bukkit player. A viewer from any other
        // platform cannot be unwrapped, and the token is handed back untouched rather than
        // pretending to have resolved it.
        if (!(viewer instanceof BukkitViewer bukkit) || !(bukkit.sender() instanceof Player player)) {
            return token;
        }

        // Guarded for the same reason as below: expansions are written by other people, and one
        // that throws would otherwise take down the whole broadcast, for every player, not just
        // the token it could not resolve.
        try {
            return PlaceholderAPI.setPlaceholders(player, token);
        } catch (RuntimeException exception) {
            return token;
        }
    }

    @Override
    public String resolveWithoutViewer(String token) {
        // PlaceholderAPI accepts a null player and server-scoped expansions answer anyway, but
        // third-party expansions are written by other people and a player-scoped one may well
        // dereference it. A broken expansion must not take an announcement down with it.
        try {
            // The cast is not redundant, whatever an IDE says: setPlaceholders is overloaded for
            // Player and for OfflinePlayer, and a bare null only lands on the Player one because
            // it happens to be the more specific of the two today.
            return PlaceholderAPI.setPlaceholders((Player) null, token);
        } catch (RuntimeException exception) {
            return token;
        }
    }

    @Override
    public boolean available() {
        return true;
    }
}
