package com.github.imdmk.automessage.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for serializing and deserializing {@link Component} instances using MiniMessage format.
 */
public final class ComponentUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private ComponentUtil() {
        throw new UnsupportedOperationException("This is utility class.");
    }

    /**
     * Deserializes a MiniMessage formatted string into a {@link Component}.
     *
     * @param text the MiniMessage string to deserialize; must not be null
     * @return the deserialized Component
     * @throws NullPointerException if a text is null
     */
    @NotNull
    public static Component deserialize(@NotNull final String text) {
        return MINI_MESSAGE.deserialize(text);
    }

    /**
     * Serializes a {@link Component} into a MiniMessage formatted string.
     *
     * @param component the Component to serialize; must not be null
     * @return the serialized MiniMessage string
     * @throws NullPointerException if component is null
     */
    @NotNull
    public static String serialize(@NotNull final Component component) {
        return MINI_MESSAGE.serialize(component);
    }
}
