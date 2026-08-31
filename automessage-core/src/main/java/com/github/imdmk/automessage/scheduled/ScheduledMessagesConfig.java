package com.github.imdmk.automessage.scheduled;

import com.eternalcode.multification.notice.Notice;
import com.eternalcode.multification.notice.resolver.NoticeResolverDefaults;
import com.eternalcode.multification.okaeri.MultificationSerdesPack;
import com.github.imdmk.automessage.config.ConfigSection;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRuleSerializer;
import com.github.imdmk.automessage.scheduled.locale.MessageTranslationSerializer;
import com.github.imdmk.automessage.scheduled.trigger.MessageTrigger;
import com.github.imdmk.automessage.scheduled.trigger.MessageTriggerSerializer;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

import java.time.Duration;
import java.util.List;

@Header({
        "# ============================================================================",
        "#                        AutoMessage - scheduledMessages.yml",
        "# ============================================================================",
        "# The announcements themselves. Every entry is one message the plugin sends.",
        "#",
        "# A message needs only a name and something to say:",
        "#",
        "#   - name: vote-reminder",
        "#     notices:",
        "#       - \"<gray>Vote for the server and claim your reward!\"",
        "#",
        "# Everything else is optional and documented above the 'messages' field below.",
        "#",
        "# Conventions used throughout this file:",
        "#   Formatting   MiniMessage - <red>, <bold>, <gradient:#ff0000:#00ff00>, ...",
        "#                See https://docs.advntr.dev/minimessage/format.html",
        "#   Durations    5s, 2m, 500ms, 1m30s. A plain number means seconds.",
        "#   Sounds       \"namespace:key SOURCE volume pitch\"",
        "#",
        "# When you are done editing:",
        "#   /automessage reload            apply the changes, no restart needed",
        "#   /automessage view <name>       see one message immediately, just for you",
        "#",
        "# Source Code:",
        "#   https://github.com/imDMK/AutoMessage",
        "#",
        "# Support development:",
        "#   GitHub Sponsors: https://github.com/sponsors/imDMK",
        "#   PayPal:          https://paypal.me/dominiksuliga",
        "#",
        "# Thank you for using AutoMessage!",
        "# ============================================================================"
})
public final class ScheduledMessagesConfig extends ConfigSection {

    @Comment({
            "#",
            "# REQUIRED",
            "#",
            "#   name      Identifies the message. Used by /automessage view <name>.",
            "#",
            "#   notices   What the player receives. One message may mix several:",
            "#",
            "#               - \"<gray>A chat line\"",
            "#               - actionbar: \"<yellow>Above the hotbar\"",
            "#               - title: \"<red>Big text\"",
            "#                 subtitle: \"<gray>Smaller text below\"",
            "#               - bossbar:",
            "#                   message: \"<green>Bar at the top\"",
            "#                   duration: 5s",
            "#                   color: RED",
            "#                   overlay: PROGRESS",
            "#               - sound: \"entity.player.levelup MASTER 1.0 1.0\"",
            "#",
            "# OPTIONAL",
            "#",
            "#   channel   Which stream in messagesDispatcher.yml sends this message.",
            "#             Each stream has its own interval. Omitted means 'default'.",
            "#",
            "#               channel: ads",
            "#",
            "#   weight    How often the WEIGHTED selector picks this message, relative",
            "#             to the others. Default 1. Use 0 to park a message without",
            "#             deleting it. Ignored by the other selectors.",
            "#",
            "#               weight: 5",
            "#",
            "#   rules     Who receives it. All rules must pass. Omit to send to",
            "#             everyone.",
            "#",
            "#               rules:",
            "#                 - type: PERMISSION",
            "#                   permission: rank.vip",
            "#                 - type: GROUP",
            "#                   group: vip",
            "#                 - type: WORLD",
            "#                   worlds: [world, world_nether]",
            "#                 - type: PLAYER_COUNT     # while 1-10 players are online",
            "#                   min: 1",
            "#                   max: 10",
            "#                 - type: PLAYTIME         # players with under 2h played",
            "#                   max: 2h",
            "#",
            "#             ANY_OF, NONE_OF and NOT combine the rules above - use them",
            "#             for anything AND cannot say, such as \"VIP or moderator\":",
            "#",
            "#               rules:",
            "#                 - type: ANY_OF",
            "#                   rules:",
            "#                     - type: PERMISSION",
            "#                       permission: rank.vip",
            "#                     - type: PERMISSION",
            "#                       permission: rank.mod",
            "#                 - type: NOT",
            "#                   rule:",
            "#                     type: PERMISSION",
            "#                     permission: automessage.hide",
            "#",
            "#   trigger   Sends the message on an event instead of on the timetable.",
            "#             A message with a trigger leaves the rotation entirely.",
            "#             Audience rules still apply.",
            "#",
            "#               trigger:",
            "#                 type: JOIN            # or FIRST_JOIN",
            "#                 delay: 3s             # let the join spam settle first",
            "#",
            "#               trigger:",
            "#                 type: PLAYER_COUNT    # fires once on reaching 100 online",
            "#                 threshold: 100",
            "#",
            "#   translations",
            "#             Per-language versions. Each player sees the one matching",
            "#             their client's language; everyone else sees 'notices'.",
            "#             A full locale (pt_br) beats a language-only one (pt).",
            "#",
            "#               translations:",
            "#                 - locale: pl",
            "#                   notices:",
            "#                     - \"<gray>Zagłosuj po nagrody!\"",
            "#",
            "# PLACEHOLDERS - usable in any text above",
            "#",
            "#   {PLAYER} {DISPLAY_NAME} {UUID} {WORLD}      about the reader",
            "#   {ONLINE} {MAX_PLAYERS} {DATE} {TIME}        about the server",
            "#",
            "#   With PlaceholderAPI installed, %any_placeholder% works too. Without it,",
            "#   such tokens are left exactly as written.",
            "#",
            "# A message using every optional field at once:",
            "#",
            "#   - name: vip-welcome",
            "#     channel: ads",
            "#     weight: 5",
            "#     notices:",
            "#       - \"<gold>Welcome back {PLAYER}! <gray>{ONLINE}/{MAX_PLAYERS} online.\"",
            "#     rules:",
            "#       - type: PERMISSION",
            "#         permission: rank.vip",
            "#     trigger:",
            "#       type: JOIN",
            "#       delay: 3s",
            "#"
    })
    public List<ScheduledMessage> messages = List.of(
            ScheduledMessageBuilder.create()
                    .name("vote-reminder")
                    .addNotices(
                            Notice.chat("<dark_gray>[<gold>!<dark_gray>] <gray>Enjoying the server? <gold>Vote <gray>for us and claim your reward!"),
                            Notice.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.MASTER, 1.0F, 1.0F)
                    )
                    .addTranslation("pl",
                            Notice.chat("<dark_gray>[<gold>!<dark_gray>] <gray>Podoba Ci się serwer? <gold>Zagłosuj <gray>i odbierz nagrodę!"),
                            Notice.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.MASTER, 1.0F, 1.0F)
                    )
                    .addTranslation("de",
                            Notice.chat("<dark_gray>[<gold>!<dark_gray>] <gray>Gefällt dir der Server? <gold>Stimme <gray>für uns und hol dir deine Belohnung!"),
                            Notice.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.MASTER, 1.0F, 1.0F)
                    )
                    .build(),

            ScheduledMessageBuilder.create()
                    .name("discord-invite")
                    .addNotice(Notice.actionbar("<dark_gray>[<blue>!<dark_gray>] <gray>Join our Discord: <blue>discord.gg/example"))
                    .addTranslation("pl", Notice.actionbar("<dark_gray>[<blue>!<dark_gray>] <gray>Dołącz na nasz Discord: <blue>discord.gg/example"))
                    .addTranslation("de", Notice.actionbar("<dark_gray>[<blue>!<dark_gray>] <gray>Tritt unserem Discord bei: <blue>discord.gg/example"))
                    .build(),

            ScheduledMessageBuilder.create()
                    .name("server-status")
                    .addNotice(Notice.chat("<dark_gray>[<green>!<dark_gray>] <gray>There are <green>{ONLINE}<gray>/<green>{MAX_PLAYERS} <gray>players online right now."))
                    .addTranslation("pl", Notice.chat("<dark_gray>[<green>!<dark_gray>] <gray>Aktualnie online: <green>{ONLINE}<gray>/<green>{MAX_PLAYERS} <gray>graczy."))
                    .addTranslation("de", Notice.chat("<dark_gray>[<green>!<dark_gray>] <gray>Aktuell online: <green>{ONLINE}<gray>/<green>{MAX_PLAYERS} <gray>Spieler."))
                    .build(),

            ScheduledMessageBuilder.create()
                    .name("event-announcement")
                    .addNotice(Notice.title(
                            "<gradient:#ffd700:#ff8c00><bold>EVENT</bold></gradient>",
                            "<gray>Starting at the arena in 5 minutes!"
                    ))
                    .addTranslation("pl", Notice.title(
                            "<gradient:#ffd700:#ff8c00><bold>EVENT</bold></gradient>",
                            "<gray>Start na arenie za 5 minut!"
                    ))
                    .addTranslation("de", Notice.title(
                            "<gradient:#ffd700:#ff8c00><bold>EVENT</bold></gradient>",
                            "<gray>Start in der Arena in 5 Minuten!"
                    ))
                    .build(),

            ScheduledMessageBuilder.create()
                    .name("restart-warning")
                    .addNotice(Notice.bossBar(BossBar.Color.RED, BossBar.Overlay.PROGRESS, Duration.ofSeconds(5),
                            "<red>The server restarts every night at 04:00"))
                    .addTranslation("pl", Notice.bossBar(BossBar.Color.RED, BossBar.Overlay.PROGRESS, Duration.ofSeconds(5),
                            "<red>Serwer restartuje się codziennie o 04:00"))
                    .addTranslation("de", Notice.bossBar(BossBar.Color.RED, BossBar.Overlay.PROGRESS, Duration.ofSeconds(5),
                            "<red>Der Server startet täglich um 04:00 Uhr neu"))
                    .build(),

            ScheduledMessageBuilder.create()
                    .name("shop-advert")
                    .addNotices(
                            Notice.chat("<dark_gray>[<light_purple>!<dark_gray>] <gray>Support the server at <light_purple>shop.example.com"),
                            Notice.actionbar("<light_purple>shop.example.com")
                    )
                    .addTranslation("pl",
                            Notice.chat("<dark_gray>[<light_purple>!<dark_gray>] <gray>Wesprzyj serwer na <light_purple>shop.example.com"),
                            Notice.actionbar("<light_purple>shop.example.com")
                    )
                    .addTranslation("de",
                            Notice.chat("<dark_gray>[<light_purple>!<dark_gray>] <gray>Unterstütze den Server auf <light_purple>shop.example.com"),
                            Notice.actionbar("<light_purple>shop.example.com")
                    )
                    .build(),

            // Only players holding the permission see this one.
            ScheduledMessageBuilder.create()
                    .name("vip-perk-reminder")
                    .addNotice(Notice.chat("<dark_gray>[<aqua>!<dark_gray>] <gray>VIP tip: use <aqua>/kit vip <gray>once every 12 hours."))
                    .addTranslation("pl", Notice.chat("<dark_gray>[<aqua>!<dark_gray>] <gray>Wskazówka VIP: użyj <aqua>/kit vip <gray>raz na 12 godzin."))
                    .addTranslation("de", Notice.chat("<dark_gray>[<aqua>!<dark_gray>] <gray>VIP-Tipp: Nutze <aqua>/kit vip <gray>alle 12 Stunden."))
                    .addRule(AudienceRule.permission("rank.vip"))
                    .build(),

            // Aimed at players who have not been here long. PLAYTIME reads the server's own
            // statistic, so it works without any other plugin.
            ScheduledMessageBuilder.create()
                    .name("newcomer-tip")
                    .addNotice(Notice.chat("<dark_gray>[<yellow>!<dark_gray>] <gray>New here? Type <yellow>/help <gray>to get started."))
                    .addTranslation("pl", Notice.chat("<dark_gray>[<yellow>!<dark_gray>] <gray>Nowy? Wpisz <yellow>/help<gray>, aby zacząć."))
                    .addTranslation("de", Notice.chat("<dark_gray>[<yellow>!<dark_gray>] <gray>Neu hier? Tippe <yellow>/help<gray>, um zu starten."))
                    .addRule(AudienceRule.playTime(Duration.ZERO, Duration.ofHours(2)))
                    .build(),

            // Not part of the rotation: this one fires when a player joins for the first time.
            ScheduledMessageBuilder.create()
                    .name("first-join-welcome")
                    .addNotice(Notice.chat("<dark_gray>[<green>!<dark_gray>] <gray>Welcome to the server, <green>{PLAYER}<gray>! You are player number <green>{ONLINE}<gray> online."))
                    .addTranslation("pl", Notice.chat("<dark_gray>[<green>!<dark_gray>] <gray>Witaj na serwerze, <green>{PLAYER}<gray>! Jesteś <green>{ONLINE}<gray> graczem online."))
                    .addTranslation("de", Notice.chat("<dark_gray>[<green>!<dark_gray>] <gray>Willkommen auf dem Server, <green>{PLAYER}<gray>! Du bist Spieler Nummer <green>{ONLINE}<gray> online."))
                    .trigger(MessageTrigger.firstJoin(Duration.ofSeconds(3)))
                    .build()
    );

    @Override
    public OkaeriSerdesPack getSerdesPack() {
        return registry -> {
            registry.register(new ScheduledMessageSerializer());
            registry.register(new AudienceRuleSerializer());
            registry.register(new MessageTriggerSerializer());
            registry.register(new MessageTranslationSerializer());
            registry.register(new MultificationSerdesPack(NoticeResolverDefaults.createRegistry()));
        };
    }

    @Override
    public String getFileName() {
        return "scheduledMessages.yml";
    }
}
