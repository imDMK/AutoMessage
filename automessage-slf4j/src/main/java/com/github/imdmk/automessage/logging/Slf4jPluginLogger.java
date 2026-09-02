package com.github.imdmk.automessage.logging;

import com.github.imdmk.automessage.platform.logger.PluginLogger;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class Slf4jPluginLogger implements PluginLogger {

    private final Logger logger;

    public Slf4jPluginLogger(Logger logger) {
        this.logger = logger;
    }

    // The message is already formatted, so these overloads are the ones that do no substitution
    // of their own - SLF4J would otherwise read a brace in the text as a placeholder.
    @Override
    public void log(Level level, String message, @Nullable Throwable throwable) {
        switch (level) {
            case INFO -> logger.info(message, throwable);
            case WARN -> logger.warn(message, throwable);
            case ERROR -> logger.error(message, throwable);
        }
    }
}
