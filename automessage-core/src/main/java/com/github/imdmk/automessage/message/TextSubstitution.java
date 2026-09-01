package com.github.imdmk.automessage.message;

import java.util.Map;
import java.util.function.UnaryOperator;

public final class TextSubstitution {

    private TextSubstitution() {
    }

    // Handed to the renderer rather than applied to the text first, because a notice is several
    // strings and only the renderer knows which of them a given part is made of.
    public static UnaryOperator<String> of(Map<String, String> placeholders) {
        if (placeholders.isEmpty()) {
            return UnaryOperator.identity();
        }
        return text -> {
            String substituted = text;
            for (final Map.Entry<String, String> placeholder : placeholders.entrySet()) {
                substituted = substituted.replace(placeholder.getKey(), placeholder.getValue());
            }
            return substituted;
        };
    }
}
