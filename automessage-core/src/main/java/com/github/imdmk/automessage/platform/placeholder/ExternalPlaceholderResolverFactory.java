package com.github.imdmk.automessage.platform.placeholder;

import com.github.imdmk.automessage.platform.logger.PluginLogger;
import org.bukkit.Server;

public final class ExternalPlaceholderResolverFactory {

    private static final String PLACEHOLDER_API = "PlaceholderAPI";

    private ExternalPlaceholderResolverFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * Decides once, at startup, whether PlaceholderAPI is available.
     *
     * <p>
     * The check is deliberately not repeated per message: asking the plugin manager on every
     * broadcast would cost more than the lookup it guards, and a plugin appearing mid-run is
     * handled by {@code /automessage reload} like every other change.
     * </p>
     */
    public static ExternalPlaceholderResolver create(Server server, PluginLogger logger) {
        if (!server.getPluginManager().isPluginEnabled(PLACEHOLDER_API)) {
            return ExternalPlaceholderResolver.disabled();
        }

        logger.info("%s found - external placeholders are enabled.", PLACEHOLDER_API);
        return new PapiPlaceholderResolver();
    }
}
