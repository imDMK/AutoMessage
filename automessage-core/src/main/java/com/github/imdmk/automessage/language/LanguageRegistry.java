package com.github.imdmk.automessage.language;

import com.github.imdmk.automessage.notice.Notice;
import com.github.imdmk.automessage.config.ConfigManager;
import com.github.imdmk.automessage.config.ConfigReloadListener;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public final class LanguageRegistry implements ConfigReloadListener {

    private static final String FOLDER = "lang";
    private static final String EXTENSION = ".yml";

    private final PluginLogger logger;
    private final ConfigManager configManager;
    private final Supplier<String> fallbackCode;

    private volatile Languages languages;

    private LanguageRegistry(PluginLogger logger, ConfigManager configManager, Supplier<String> fallbackCode) {
        this.logger = logger;
        this.configManager = configManager;
        this.fallbackCode = fallbackCode;
    }

    public static LanguageRegistry load(
            ConfigManager configManager,
            PluginLogger logger,
            Supplier<String> fallbackCode
    ) {
        final LanguageRegistry registry = new LanguageRegistry(logger, configManager, fallbackCode);
        registry.languages = registry.read(Map.of());

        return registry;
    }

    @Override
    public void onConfigReload() {
        this.languages = read(languages.byCode());
    }

    private Languages read(Map<String, LanguageConfig> existing) {
        final Map<String, LanguageConfig> byCode = new LinkedHashMap<>(existing);

        // Written out first so a fresh install is not an empty folder; discovery below cannot
        // create a file that does not exist yet.
        for (final String code : ShippedLanguages.CODES) {
            open(byCode, code);
        }

        // Anything the administrator added themselves. Loading these after the shipped ones means
        // a hand-written lang/en.yml is already open and is not opened twice.
        for (final String code : discover(configManager.dataFolder())) {
            open(byCode, code);
        }

        if (byCode.isEmpty()) {
            throw new IllegalStateException(
                    "No language files could be loaded from lang/ - AutoMessage has nothing to say."
            );
        }

        final String wanted = fallbackCode.get();
        LanguageConfig fallback = byCode.get(LanguageCode.normalize(wanted));

        if (fallback == null) {
            fallback = byCode.values().iterator().next();
            logger.warn(
                    "Fallback language '%s' has no file in lang/, using '%s' instead.",
                    wanted,
                    fallback.code()
            );
        }

        if (byCode.size() != existing.size()) {
            logger.info("Loaded %d language(s): %s.", byCode.size(), String.join(", ", byCode.keySet()));
        }

        return new Languages(Map.copyOf(byCode), fallback);
    }

    private void open(Map<String, LanguageConfig> byCode, String code) {
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

    public LanguageConfig provide(Locale locale) {
        return provide(LanguageCode.of(locale));
    }

    public LanguageConfig provide(String rawCode) {
        final Languages current = languages;
        final String code = LanguageCode.normalize(rawCode);

        final LanguageConfig exact = current.byCode().get(code);
        if (exact != null) {
            return exact;
        }

        final LanguageConfig language = current.byCode().get(LanguageCode.language(code));

        return language != null ? language : current.fallback();
    }

    @Nullable
    public List<Notice> announcement(String messageName, String rawCode) {
        return announcement(messageName, provide(rawCode));
    }

    // Takes the language already resolved, so a caller serving many players at once resolves it
    // once for the whole group rather than once per player.
    @Nullable
    public List<Notice> announcement(String messageName, LanguageConfig language) {
        final List<Notice> translated = language.announcement(messageName);

        if (translated != null) {
            return translated;
        }

        return languages.fallback().announcement(messageName);
    }

    public LanguageConfig fallback() {
        return languages.fallback();
    }

    @Unmodifiable
    public List<LanguageConfig> all() {
        return List.copyOf(languages.byCode().values());
    }

    private record Languages(Map<String, LanguageConfig> byCode, LanguageConfig fallback) {
    }
}
