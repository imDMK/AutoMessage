package com.github.imdmk.automessage.platform.discord;

public final class DiscordPayload {

    public static final int CONTENT_LIMIT = 2000;

    private DiscordPayload() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static String build(String content, String username, String avatarUrl) {
        final StringBuilder json = new StringBuilder(128);

        json.append("{\"content\":\"").append(escape(truncate(content))).append('"');

        if (username != null && !username.isBlank()) {
            json.append(",\"username\":\"").append(escape(username)).append('"');
        }

        if (avatarUrl != null && !avatarUrl.isBlank()) {
            json.append(",\"avatar_url\":\"").append(escape(avatarUrl)).append('"');
        }

        // Nothing the server announces should be able to ping everyone in a Discord guild.
        json.append(",\"allowed_mentions\":{\"parse\":[]}}");

        return json.toString();
    }

    static String truncate(String content) {
        return content.length() <= CONTENT_LIMIT ? content : content.substring(0, CONTENT_LIMIT);
    }

    static String escape(String value) {
        final StringBuilder escaped = new StringBuilder(value.length() + 16);

        for (int i = 0; i < value.length(); i++) {
            final char character = value.charAt(i);

            switch (character) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                default -> {
                    if (character < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
                }
            }
        }

        return escaped.toString();
    }
}
