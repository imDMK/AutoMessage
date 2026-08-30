package com.github.imdmk.automessage.platform.placeholder;

import org.bukkit.entity.Player;

/**
 * Resolves the {@code %...%} tokens AutoMessage does not own itself.
 */
public interface ExternalPlaceholderResolver {

    /**
     * @return the resolved value, or the token itself when nothing can resolve it
     */
    String resolve(Player viewer, String token);

    boolean available();

    /** Used when PlaceholderAPI is not installed; leaves every token exactly as written. */
    static ExternalPlaceholderResolver disabled() {
        return new ExternalPlaceholderResolver() {

            @Override
            public String resolve(Player viewer, String token) {
                return token;
            }

            @Override
            public boolean available() {
                return false;
            }
        };
    }
}
