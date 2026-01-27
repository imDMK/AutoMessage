package com.github.imdmk.automessage.platform.adventure;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class AdventureFormatter {

    private AdventureFormatter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Component format(String input, AdventurePlaceholders placeholders) {
        return format(AdventureComponents.text(input), placeholders);
    }


    public static List<Component> format(List<Component> components, AdventurePlaceholders placeholders) {
        return components.stream()
                .map(component -> format(component, placeholders))
                .collect(Collectors.toList());
    }

    public static Component format(Component input, AdventurePlaceholders placeholders) {
        if (placeholders.isEmpty()) {
            return input;
        }

        // Sort keys by descending length to avoid substring overlap
        final List<Map.Entry<String, Component>> ordered = placeholders.asMap().entrySet().stream()
                .sorted(Comparator.<Map.Entry<String, Component>>comparingInt(e -> e.getKey().length()).reversed())
                .toList();

        Component out = input;
        for (final Map.Entry<String, Component> e : ordered) {
            final String key = e.getKey();
            final Component replacement = e.getValue();

            final TextReplacementConfig config = TextReplacementConfig.builder()
                    .matchLiteral(key)
                    .replacement(replacement)
                    .build();

            out = out.replaceText(config);
        }

        return out;
    }
}
