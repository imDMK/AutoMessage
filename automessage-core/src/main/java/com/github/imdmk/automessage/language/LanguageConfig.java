package com.github.imdmk.automessage.language;

import com.github.imdmk.automessage.notice.Notice;
import com.github.imdmk.automessage.notice.NoticeSerializer;
import com.github.imdmk.automessage.config.ConfigSection;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Header({
        "# ============================================================================",
        "#                            AutoMessage - language file",
        "# ============================================================================",
        "# Every piece of text AutoMessage can show, in one language.",
        "#",
        "# Adding a language:",
        "#   1. Copy this file to lang/<code>.yml - for example lang/fr.yml.",
        "#   2. Translate the values.",
        "#   3. /automessage reload",
        "#",
        "# The code is what the player's Minecraft client reports: 'fr', or 'pt_br'",
        "# when a country matters. A player is served the closest match - pt_br first,",
        "# then pt, then the fallback language set in config.yml.",
        "#",
        "# Two kinds of text live here:",
        "#",
        "#   commands        Replies to /automessage. Fixed set of keys.",
        "#",
        "#   announcements   The scheduled announcements themselves, keyed by the name",
        "#                   they carry in scheduledMessages.yml. That file decides",
        "#                   WHEN a message is sent and to WHOM; this one decides what",
        "#                   it SAYS. A name missing here falls back to the fallback",
        "#                   language, so translating is never all-or-nothing.",
        "#",
        "# Formatting is MiniMessage: <red>, <bold>, <gradient:#ff0000:#00ff00>, ...",
        "#",
        "# Source Code:",
        "#   https://github.com/imDMK/AutoMessage",
        "#",
        "# Support development:",
        "#   GitHub Sponsors: https://github.com/sponsors/imDMK",
        "#   PayPal:          https://paypal.me/dominiksuliga",
        "#",
        "# ============================================================================"
})
public final class LanguageConfig extends ConfigSection {

    @Comment({"#", "# Replies to /automessage commands.", "#"})
    public CommandMessages commands = new CommandMessages();

    @Comment({
            "#",
            "# The announcements, keyed by their name in scheduledMessages.yml.",
            "#",
            "# Each value is the same list of notices that file used to hold inline:",
            "#",
            "#   announcements:",
            "#     vote-reminder:",
            "#       - \"<gray>Vote for the server!\"",
            "@requires SOUND_NOTICE",
            "#       - sound: \"entity.experience_orb.pickup MASTER 1.0 1.0\"",
            "@end",
            "@requires TITLE_NOTICE",
            "#     event-announcement:",
            "#       - title: \"<gold>EVENT\"",
            "#         subtitle: \"<gray>Starting soon!\"",
            "@end",
            "#"
    })
    public Map<String, List<Notice>> announcements = new LinkedHashMap<>();

    private transient String fileName = "lang/en.yml";

    private transient String code = "en";

    public LanguageConfig() {
    }

    public static LanguageConfig forCode(String code) {
        final LanguageConfig config = new LanguageConfig();

        config.code = LanguageCode.normalize(code);
        config.fileName = "lang/" + config.code + ".yml";

        return config;
    }

    public String code() {
        return code;
    }

    @Nullable
    public List<Notice> announcement(String name) {
        final List<Notice> notices = announcements.get(name);

        // An entry left empty in the file means "not translated", not "send nothing".
        return notices == null || notices.isEmpty() ? null : notices;
    }

    @Override
    public OkaeriSerdesPack getSerdesPack() {
        return registry -> registry.register(new NoticeSerializer());
    }

    @Override
    public String getFileName() {
        return fileName;
    }
}
