package com.github.imdmk.automessage.message;

import com.github.imdmk.automessage.config.ConfigManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Picks the message file a given player should read.
 *
 * <p>
 * Multification hands the viewer's {@link Locale} to the translation provider, so the choice needs
 * no command and nothing stored per player - a client set to Polish simply gets the Polish file.
 * </p>
 */
public final class MessageConfigRegistry {

    /** Read when a player's language has no file of its own. */
    private final MessageConfig fallback;

    /**
     * Keyed by language tag in lower case: {@code pl}, {@code de}, and full tags such as
     * {@code pt_br} should a server add one.
     */
    private final Map<String, MessageConfig> byLanguage = new LinkedHashMap<>();

    private MessageConfigRegistry(MessageConfig fallback) {
        this.fallback = fallback;
    }

    /**
     * Loads every shipped language. English doubles as the fallback, which is why it is also the
     * file that keeps the plain {@code messages.yml} name.
     */
    public static MessageConfigRegistry load(ConfigManager configManager) {
        final MessageConfigRegistry registry =
                new MessageConfigRegistry(configManager.create(ENMessageConfig.class));

        registry.byLanguage.put("en", registry.fallback);
        registry.byLanguage.put("pl", configManager.create(PLMessageConfig.class));
        registry.byLanguage.put("de", configManager.create(DEMessageConfig.class));

        return registry;
    }

    /**
     * @param locale language the viewer's client is running, may be null for the console
     * @return the closest match, falling back to English
     */
    public MessageConfig provide(Locale locale) {
        if (locale == null) {
            return fallback;
        }

        // An exact match first, so a server adding pt_br is preferred over a plain pt.
        final MessageConfig exact = byLanguage.get(tag(locale));
        if (exact != null) {
            return exact;
        }

        final MessageConfig language = byLanguage.get(locale.getLanguage().toLowerCase(Locale.ROOT));

        return language != null ? language : fallback;
    }

    /** @return every loaded configuration, English first */
    public List<MessageConfig> all() {
        return List.copyOf(byLanguage.values());
    }

    private static String tag(Locale locale) {
        return locale.toString().toLowerCase(Locale.ROOT).replace('-', '_');
    }
}
