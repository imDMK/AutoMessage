package com.github.imdmk.automessage.config;

import com.github.imdmk.automessage.platform.capability.Capabilities;
import com.github.imdmk.automessage.platform.capability.Capability;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class CommentDirectives {

    private static final String REQUIRES = "@requires";
    private static final String END = "@end";

    private CommentDirectives() {
    }

    static String[] apply(String[] lines, Capabilities capabilities) {
        if (lines == null || !hasDirective(lines)) {
            return lines;
        }

        final List<String> kept = new ArrayList<>(lines.length);

        Capability required = null;

        for (final String line : lines) {
            final String directive = line.strip();

            if (directive.startsWith(REQUIRES)) {
                if (required != null) {
                    throw new IllegalStateException(
                            "Nested " + REQUIRES + " in a configuration comment: " + directive
                    );
                }

                required = capabilityOf(directive);
                continue;
            }

            if (directive.equals(END)) {
                if (required == null) {
                    throw new IllegalStateException(
                            END + " without a matching " + REQUIRES + " in a configuration comment"
                    );
                }

                required = null;
                continue;
            }

            if (required == null || capabilities.supports(required)) {
                kept.add(line);
            }
        }

        if (required != null) {
            throw new IllegalStateException(
                    REQUIRES + " " + required + " was never closed with " + END
            );
        }

        return kept.toArray(String[]::new);
    }

    private static boolean hasDirective(String[] lines) {
        for (final String line : lines) {
            final String directive = line.strip();

            if (directive.startsWith(REQUIRES) || directive.equals(END)) {
                return true;
            }
        }

        return false;
    }

    private static Capability capabilityOf(String directive) {
        final String name = directive.substring(REQUIRES.length()).strip().toUpperCase(Locale.ROOT);

        try {
            return Capability.valueOf(name);
        } catch (IllegalArgumentException exception) {
            // A typo here would silently keep documentation nobody can act on, which is the exact
            // failure this class exists to prevent - so it is an error, not a fallback.
            throw new IllegalStateException(
                    "Unknown capability '" + name + "' in a configuration comment", exception
            );
        }
    }
}
