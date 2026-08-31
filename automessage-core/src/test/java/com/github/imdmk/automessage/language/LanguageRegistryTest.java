package com.github.imdmk.automessage.language;

import com.github.imdmk.automessage.config.ConfigManager;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class LanguageRegistryTest {

    @TempDir
    Path dataFolder;

    private ConfigManager configManager;

    @BeforeEach
    void setUp() {
        configManager = new ConfigManager(mock(PluginLogger.class), dataFolder.toFile());
    }

    private LanguageRegistry load() {
        return LanguageRegistry.load(configManager, mock(PluginLogger.class), List.of("en", "pl", "de"), "en");
    }

    @Test
    @DisplayName("resolves the locale Bukkit actually produces, not a hand-built one")
    void resolvesTheLocaleBukkitProduces() {
        LanguageRegistry registry = load();

        // This is the whole bug the previous design shipped with. Bukkit reports "pl_pl" and
        // Multification wraps it in new Locale("pl_pl"), which puts the entire string in the
        // language field - so getLanguage() answers "pl_pl", never "pl", and every Polish player
        // silently got English. A hand-built Locale.of("pl","PL") hides that, which is exactly
        // why the old test passed.
        Locale asMultificationBuildsIt = new Locale("pl_pl");

        assertThat(registry.provide(asMultificationBuildsIt).code()).isEqualTo("pl");
    }

    @Test
    @DisplayName("resolves every shape a language code arrives in")
    void resolvesEveryShape() {
        LanguageRegistry registry = load();

        for (String code : List.of("pl", "PL", "pl_pl", "pl_PL", "pl-PL", "  pl_pl  ")) {
            assertThat(registry.provide(code).code())
                    .withFailMessage("expected '%s' to resolve to Polish", code)
                    .isEqualTo("pl");
        }
    }

    @Test
    @DisplayName("a language nobody translated falls back")
    void fallsBack() {
        LanguageRegistry registry = load();

        assertThat(registry.provide("ja_jp").code()).isEqualTo("en");
        assertThat(registry.provide((String) null).code()).isEqualTo("en");
        assertThat(registry.provide((Locale) null).code()).isEqualTo("en");
    }

    @Test
    @DisplayName("picks up a language file the administrator dropped in, with no code change")
    void discoversLanguagesOnDisk() throws Exception {
        // The point of the redesign: adding French must not mean writing Java.
        Files.createDirectories(dataFolder.resolve("lang"));
        Files.writeString(dataFolder.resolve("lang/fr.yml"), """
                commands:
                  viewPlayerOnly: "<red>Seuls les joueurs peuvent voir un apercu."
                announcements:
                  vote-reminder:
                    - "<gray>Votez pour le serveur!"
                """);

        LanguageRegistry registry = load();

        assertThat(registry.provide("fr_fr").code()).isEqualTo("fr");
        assertThat(registry.announcement("vote-reminder", "fr_fr")).isNotNull();
    }

    @Test
    @DisplayName("a full code beats a bare language")
    void fullCodeWins() throws Exception {
        Files.createDirectories(dataFolder.resolve("lang"));
        Files.writeString(dataFolder.resolve("lang/pt.yml"), "announcements: {}\n");
        Files.writeString(dataFolder.resolve("lang/pt_br.yml"), "announcements: {}\n");

        LanguageRegistry registry = load();

        assertThat(registry.provide("pt_br").code()).isEqualTo("pt_br");
        assertThat(registry.provide("pt_pt").code()).isEqualTo("pt");
    }

    @Test
    @DisplayName("an announcement missing from a language falls back rather than sending nothing")
    void announcementFallsBack() throws Exception {
        Files.createDirectories(dataFolder.resolve("lang"));
        Files.writeString(dataFolder.resolve("lang/fr.yml"), "announcements: {}\n");

        LanguageRegistry registry = load();

        // French translates nothing, so a French player still receives the English text.
        assertThat(registry.announcement("vote-reminder", "fr_fr"))
                .isEqualTo(registry.announcement("vote-reminder", "en"));
    }

    @Test
    @DisplayName("every shipped announcement has text in every shipped language")
    void shippedLanguagesAreComplete() {
        LanguageRegistry registry = load();

        List<String> names = List.copyOf(registry.fallback().announcements.keySet());
        assertThat(names).isNotEmpty();

        for (LanguageConfig language : registry.all()) {
            for (String name : names) {
                assertThat(language.announcement(name))
                        .withFailMessage("'%s' has no text in lang/%s.yml", name, language.code())
                        .isNotNull();
            }
        }
    }
}
