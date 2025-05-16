package com.github.imdmk.automessage.configuration;

public final class ConfigurationLoadException extends RuntimeException {
    public ConfigurationLoadException(Throwable cause) {
        super("Failed to load configuration", cause);
    }

    public ConfigurationLoadException(String message) {
        super(message);
    }

    public ConfigurationLoadException(String message, Throwable cause) {
        super(message, cause);
    }

}
