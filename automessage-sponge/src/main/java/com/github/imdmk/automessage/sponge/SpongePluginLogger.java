package com.github.imdmk.automessage.sponge;

import com.github.imdmk.automessage.platform.logger.PluginLogger;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class SpongePluginLogger implements PluginLogger {

    private final Logger logger;

    public SpongePluginLogger(Logger logger) {
        this.logger = logger;
    }

    // The message is already formatted, so these overloads are the ones that do no substitution
    // of their own - Log4j would otherwise read a brace in the text as a placeholder.
    @Override
    public void log(Level level, String message, @Nullable Throwable throwable) {
        switch (level) {
            case INFO -> logger.info(message, throwable);
            case WARN -> logger.warn(message, throwable);
            case ERROR -> logger.error(message, throwable);
        }
    }
}
