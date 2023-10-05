package com.github.imdmk.automessage.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ComponentUtil {

    private static final CharSequence LEGACY_CHAR = "§";

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.legacySection();

    private ComponentUtil() {
        throw new UnsupportedOperationException("This is utility class.");
    }

    public static Component deserialize(String text) {
        return text.contains(LEGACY_CHAR)
                ? LEGACY_COMPONENT_SERIALIZER.deserialize(text)
                : MINI_MESSAGE.deserialize(text);
    }
}
