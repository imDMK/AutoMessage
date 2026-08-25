package com.github.imdmk.automessage.platform.litecommands.argument;

/**
 * Result produced when a command argument does not match any configured scheduled message.
 *
 * @param name the name that was typed
 */
public record UnknownScheduledMessage(String name) {
}
