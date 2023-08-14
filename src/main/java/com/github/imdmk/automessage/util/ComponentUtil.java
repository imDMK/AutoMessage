package com.github.imdmk.automessage.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;

public class ComponentUtil {

    public static final CharSequence LEGACY_CHAR = "§";

    private ComponentUtil() {
        throw new UnsupportedOperationException("This is utility class.");
    }

    public static Component deserialize(String text) {
        return text.contains(LEGACY_CHAR)
                ? LegacyComponentSerializer.legacySection().deserialize(text)
                : MiniMessage.miniMessage().deserialize(text);
    }

    public static List<Component> deserialize(List<String> strings) {
        return strings.stream()
                .map(ComponentUtil::deserialize)
                .toList();
    }
}
