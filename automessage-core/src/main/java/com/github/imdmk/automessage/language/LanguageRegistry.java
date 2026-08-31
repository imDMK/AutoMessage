package com.github.imdmk.automessage.language;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.config.ConfigManager;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Every language the server has, and the rules for choosing between them.
 *
 * <p>
 * Languages are found by looking in the {@code lang/} folder, not by being listed in code. An
 * administrator adds French by dropping in {@code lang/fr.yml}; nothing is compiled and nothing
 * else has to be told about it.
 * </p>
 */
public final class LanguageRegistry {

    private static final String FOLDER = "lang";
    private static final String EXTENSION = ".yml";

    private final PluginLogger logger;

    /** Keyed by normalised code: {@code en}, {@code pl}, {@code pt_br}. */
    private final Map<String, LanguageConfig> byCode = new LinkedHashMap<>();

    private LanguageConfig fallback;

    private LanguageRegistry(PluginLogger logger) {
        this.logger = logger;
    }

    /**
     * @param shipped codes written out on first run, so a fresh install is not an empty folder
     * @param fallbackCode language served to players whose own is missing
     */
    public static LanguageRegistry load(
            ConfigManager configManager,
            PluginLogger logger,
            List<String> shipped,
            String fallbackCode
    ) {
        final LanguageRegistry registry = new LanguageRegistry(logger);

        for (final String code : shipped) {
            registry.open(configManager, code);
        }

        // Anything the administrator added themselves. Loading these after the shipped ones means
        // a hand-written lang/en.yml is already open and is not opened twice.
        for (final String code : registry.discover(configManager.dataFolder())) {
            registry.open(configManager, code);
        }

        registry.fallback = registry.byCode.get(LanguageCode.normalize(fallbackCode));

        if (registry.fallback == null) {
            registry.fallback = registry.byCode.values().iterator().next();
            logger.warn(
                    "Fallback language '%s' has no file in lang/, using '%s' instead.",
                    fallbackCode,
                    registry.fallback.code()
            );
        }

        logger.info("Loaded %d language(s): %s.", registry.byCode.size(), String.join(", ", registry.byCode.keySet()));

        return registry;
    }

    private void open(ConfigManager configManager, String code) {
        final String normalized = LanguageCode.normalize(code);

        if (normalized.isEmpty() || byCode.containsKey(normalized)) {
            return;
        }

        final LanguageConfig config = LanguageConfig.forCode(normalized);

        // Seeded before the file is opened: create() loads whatever is on disk over the top, so
        // a server's own edits always win and a shipped language only fills in a missing file.
        ShippedLanguages.applyDefaults(config);

        byCode.put(normalized, configManager.create(config));
    }

    /** @return the codes of every {@code lang/*.yml} already on disk */
    private List<String> discover(File dataFolder) {
        final File folder = new File(dataFolder, FOLDER);
        final File[] files = folder.listFiles();

        if (files == null) {
            return List.of();
        }

        final List<String> codes = new ArrayList<>();

        for (final File file : files) {
            final String name = file.getName();

            if (file.isFile() && name.endsWith(EXTENSION)) {
                codes.add(name.substring(0, name.length() - EXTENSION.length()));
            }
        }

        return codes;
    }

    /**
     * Picks the file for a viewer.
     *
     * <p>
     * A full code wins over a bare language, so a server that adds {@code pt_br} serves it to
     * Brazilian clients while Portuguese ones still get {@code pt}.
     * </p>
     */
    public LanguageConfig provide(Locale locale) {
        return provide(LanguageCode.of(locale));
    }

    public LanguageConfig provide(String rawCode) {
        final String code = LanguageCode.normalize(rawCode);

        final LanguageConfig exact = byCode.get(code);
        if (exact != null) {
            return exact;
        }

        final LanguageConfig language = byCode.get(LanguageCode.language(code));

        return language != null ? language : fallback;
    }

    /**
     * @return the announcement text for this viewer, falling back to the fallback language and
     *         then to null when nobody translates it
     */
    @Nullable
    public List<Notice> announcement(String messageName, String rawCode) {
        final List<Notice> translated = provide(rawCode).announcement(messageName);

        if (translated != null) {
            return translated;
        }

        return fallback.announcement(messageName);
    }

    public LanguageConfig fallback() {
        return fallback;
    }

    @Unmodifiable
    public List<LanguageConfig> all() {
        return List.copyOf(byCode.values());
    }
}
