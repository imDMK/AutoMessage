package com.github.imdmk.automessage.platform.logger;

import org.intellij.lang.annotations.PrintFormat;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public interface PluginLogger {

    enum Level {
        INFO,
        WARN,
        ERROR
    }

    // The only method a platform implements. Formatting used to live in each of them instead,
    // which is how one ended up formatting under the default locale and the others under ROOT.
    void log(Level level, String message, @Nullable Throwable throwable);

    default void info(@PrintFormat String message, Object... args) {
        log(Level.INFO, format(message, args), null);
    }

    default void warn(@PrintFormat String message, Object... args) {
        log(Level.WARN, format(message, args), null);
    }

    default void warn(Throwable throwable, @PrintFormat String message, Object... args) {
        log(Level.WARN, format(message, args), throwable);
    }

    default void error(@PrintFormat String message, Object... args) {
        log(Level.ERROR, format(message, args), null);
    }

    default void error(Throwable throwable, @PrintFormat String message, Object... args) {
        log(Level.ERROR, format(message, args), throwable);
    }

    // Passed through untouched when there is nothing to substitute, so a message that happens to
    // contain a percent sign is not read as a format specifier.
    private static String format(String message, Object... args) {
        return args.length == 0 ? message : String.format(Locale.ROOT, message, args);
    }
}
