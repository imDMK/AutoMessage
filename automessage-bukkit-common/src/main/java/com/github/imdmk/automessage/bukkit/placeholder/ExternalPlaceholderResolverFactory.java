package com.github.imdmk.automessage.bukkit.placeholder;

import com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolver;

import com.github.imdmk.automessage.platform.logger.PluginLogger;
import org.bukkit.Server;

public final class ExternalPlaceholderResolverFactory {

    private static final String PLACEHOLDER_API = "PlaceholderAPI";

    private ExternalPlaceholderResolverFactory() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static ExternalPlaceholderResolver create(Server server, PluginLogger logger) {
        if (!server.getPluginManager().isPluginEnabled(PLACEHOLDER_API)) {
            return ExternalPlaceholderResolver.disabled();
        }

        logger.info("%s found - external placeholders are enabled.", PLACEHOLDER_API);
        return new PapiPlaceholderResolver();
    }
}
