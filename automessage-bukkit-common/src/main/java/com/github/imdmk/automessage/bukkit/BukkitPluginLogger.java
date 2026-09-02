package com.github.imdmk.automessage.bukkit;

import com.github.imdmk.automessage.platform.logger.PluginLogger;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public final class BukkitPluginLogger implements PluginLogger {

    private final Logger logger;

    public BukkitPluginLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void log(Level level, String message, @Nullable Throwable throwable) {
        logger.log(toJulLevel(level), message, throwable);
    }

    private static java.util.logging.Level toJulLevel(Level level) {
        return switch (level) {
            case INFO -> java.util.logging.Level.INFO;
            case WARN -> java.util.logging.Level.WARNING;
            case ERROR -> java.util.logging.Level.SEVERE;
        };
    }
}
