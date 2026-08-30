package com.github.imdmk.automessage.scheduled;

import com.eternalcode.multification.notice.Notice;
import com.eternalcode.multification.notice.resolver.NoticeResolverDefaults;
import com.eternalcode.multification.okaeri.MultificationSerdesPack;
import com.github.imdmk.automessage.config.ConfigSection;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRuleSerializer;
import com.github.imdmk.automessage.scheduled.locale.MessageTranslationSerializer;
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
        "#                        AutoMessage — scheduledMessages.yml",
        "# ============================================================================",
        "# This file defines all automatically dispatched messages used by AutoMessage.",
        "# Each entry represents one scheduled announcement, which may contain multiple",
        "# message formats such as chat, actionbar, title, bossbar, and sound notices.",
        "#",
        "# How it works:",
        "#  • Messages are dispatched in a sequence, based on the selector strategy",
        "#    configured in messagesDispatcher.yml.",
        "#  • Each message may contain access rules (audience filters) to restrict",
        "#    delivery to specific players (groups, permissions, etc.).",
        "#",
        "# Structure of a scheduled message entry:",
        "#  name:      Unique identifier of the message.",
        "#  notices:   One or more notices to send using Multification.",
        "#             Supported notice types:",
        "#               - Chat",
        "#               - Actionbar",
        "#               - Title / Subtitle",
        "#               - BossBar",
        "#               - Sound",
        "#",
        "#  trigger:   Optional. Makes the message fire on an event instead of on the",
        "#             timetable. A message with a trigger leaves the rotation entirely.",
        "#             Supported triggers:",
        "#               • JOIN         -> sent to a player as they join",
        "#               • FIRST_JOIN   -> sent the first time a player ever joins",
        "#               • PLAYER_COUNT -> broadcast when the online count reaches a threshold",
        "#  weight:    Optional relative frequency for the WEIGHTED selector (default 1).",
        "#  channel:   Optional. Which announcement stream this message belongs to, matching",
        "#             a channel declared in messagesDispatcher.yml. Omitted means 'default',",
        "#             which is the stream configured by the top-level timing in that file.",
        "#  rules:     Optional audience restrictions. Rules listed on a message are combined",
        "#             with AND; the ANY_OF / NONE_OF / NOT rules nest others to express",
        "#             anything more involved.",
        "#  translations:",
        "#             Optional per-language variants. Each player receives the variant",
        "#             matching the language their client runs in, and anyone whose",
        "#             language is not listed gets the default 'notices'.",
        "#             Supported rule types:",
        "#               • PERMISSION   -> players holding this permission",
        "#               • GROUP        -> players in the given group",
        "#               • WORLD        -> players standing in one of the listed worlds",
        "#               • PLAYER_COUNT -> only while the online count is in range",
        "#               • PLAYTIME     -> only players whose playtime is in range",
        "#               • ANY_OF       -> passes when at least one nested rule passes",
        "#               • NONE_OF      -> passes only when no nested rule passes",
        "#               • NOT          -> inverts a single nested rule",
        "#",
        "# Placeholders:",
        "#  Built-in, available with no other plugin installed:",
        "#    {PLAYER}        Name of the player receiving the message",
        "#    {DISPLAY_NAME}  Their display name, formatting included",
        "#    {UUID}          Their unique id",
        "#    {WORLD}         The world they are currently in",
        "#    {ONLINE}        Players currently online",
        "#    {MAX_PLAYERS}   Player slots configured for the server",
        "#    {DATE}          Current date, dd.MM.yyyy",
        "#    {TIME}          Current time, HH:mm",
        "#",
        "#  With PlaceholderAPI installed, any %placeholder% is resolved too, for example",
        "#  %vault_eco_balance% or %server_tps%. Without it, such tokens are left as written.",
        "#",
        "#  Only the placeholders a message actually mentions are resolved, so messages that",
        "#  use none cost nothing extra to send.",
        "#",
        "# Editing recommendations:",
        "#  • You may freely add or remove message entries.",
        "#  • MiniMessage formatting (<red>, <yellow>, <rainbow>, etc.) is fully supported.",
        "#  • Sound format: \"namespace:key SOURCE volume pitch\".",
        "#  • Duration fields support values like: 5s, 2m, 500ms, 1m30s.",
        "#    A plain number without a unit is read as seconds.",
        "#",
        "# After making changes, reload the plugin via:",
        "#   /automessage reload",
        "#",
        "# To check how an entry looks in-game without waiting for its turn, use:",
        "#   /automessage view <name>",
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
            "# List of scheduled messages automatically dispatched by the plugin.",
            "#",
            "# Each message entry consists of the following fields:",
            "#",
            "#   name:       Unique identifier for internal use and debugging.",
            "#",
            "#   notices:    One or more message types to send to the player.",
            "#               Supported notice formats:",
            "#                 • Chat message:",
            "#                       - \"<gray>Hello world!\"",
            "#",
            "#                 • Actionbar:",
            "#                       - actionbar: \"<yellow>Actionbar message!\"",
            "#",
            "#                 • Title:",
            "#                       - title: \"<red>Title text\"",
            "#                         subtitle: \"<gray>Subtitle text\"",
            "#",
            "#                 • BossBar:",
            "#                       - bossbar:",
            "#                           message: \"<green>BossBar text\"",
            "#                           duration: 5s",
            "#                           color: RED",
            "#                           overlay: PROGRESS",
            "#",
            "#                 • Sound:",
            "#                       - sound: \"minecraft:entity.player.levelup MASTER 1.0 1.0\"",
            "#",
            "#   trigger:    Optional. Fires the message on an event rather than on the",
            "#               timetable; such a message never appears in the rotation.",
            "#",
            "#                 • Greet a player three seconds after they join, so the",
            "#                   message is not buried by the server's own join spam:",
            "#                       trigger:",
            "#                         type: JOIN",
            "#                         delay: 3s",
            "#",
            "#                 • Welcome a brand-new player:",
            "#                       trigger:",
            "#                         type: FIRST_JOIN",
            "#                         delay: 5s",
            "#",
            "#                 • Announce a milestone. Fires once when the count reaches the",
            "#                   threshold and rearms only after it drops back below:",
            "#                       trigger:",
            "#                         type: PLAYER_COUNT",
            "#                         threshold: 100",
            "#",
            "#               Audience rules still apply to triggered messages.",
            "#   weight:     Optional. Relative frequency, used only by the WEIGHTED",
            "#               selector. Defaults to 1 when omitted, so leaving it out keeps",
            "#               every message equally likely. A message of weight 5 appears",
            "#               five times as often as one of weight 1; weight 0 parks the",
            "#               message without deleting it.",
            "#",
            "#   channel:    Optional. Name of the announcement channel this message joins,",
            "#               matched ignoring case. Each channel has its own period and",
            "#               rotation, declared in messagesDispatcher.yml. Leave it out and",
            "#               the message uses the default channel.",
            "#   translations:",
            "#               Optional. Per-language versions of the same announcement. The",
            "#               locale is matched against the one the player's client reports,",
            "#               so no command or per-player setting is needed.",
            "#",
            "#               A full locale beats a language-only entry, which lets you write",
            "#               one Portuguese text and still override it for Brazil:",
            "#",
            "#                 translations:",
            "#                   - locale: pl",
            "#                     notices:",
            "#                       - \"<gray>Zaglosuj po nagrody!\"",
            "#                   - locale: pt_br",
            "#                     notices:",
            "#                       - \"<gray>Vote por recompensas!\"",
            "#",
            "#               Players whose language is not listed receive 'notices' above.",
            "#",
            "#   rules:      Conditions restricting who will receive this message.",
            "#               Supported audience rules:",
            "#                 • Permission rule:",
            "#                       - type: PERMISSION",
            "#                         permission: myplugin.vip",
            "#",
            "#                 • Group rule:",
            "#                       - type: GROUP",
            "#                         group: vip",
            "#",
            "#                 • World rule:",
            "#                       - type: WORLD",
            "#                         worlds:",
            "#                           - world",
            "#                           - world_nether",
            "#",
            "#                 • Player-count rule (both bounds optional, inclusive):",
            "#                       - type: PLAYER_COUNT",
            "#                         min: 1",
            "#                         max: 10",
            "#",
            "#                 • Playtime rule (both bounds optional, inclusive).",
            "#                   Uses the same time units as messagesDispatcher.yml:",
            "#                       - type: PLAYTIME",
            "#                         max: 2h",
            "#",
            "#                 • Combining rules - VIP or moderator, but not opted out:",
            "#                       - type: ANY_OF",
            "#                         rules:",
            "#                           - type: PERMISSION",
            "#                             permission: rank.vip",
            "#                           - type: PERMISSION",
            "#                             permission: rank.mod",
            "#                       - type: NOT",
            "#                         rule:",
            "#                           type: PERMISSION",
            "#                           permission: automessage.hide",
            "#",
            "# Example full message entry:",
            "#",
            "#   - name: example-message",
            "#     notices:",
            "#       - \"<green>Hello from AutoMessage!\"",
            "#       - actionbar: \"<yellow>Actionbar example\"",
            "#       - title: \"<red>Warning\"",
            "#         subtitle: \"<gray>Something happened\"",
            "#       - bossbar:",
            "#           message: \"<green>BossBar example\"",
            "#           duration: 3s",
            "#           color: BLUE",
            "#           overlay: PROGRESS",
            "#       - sound: \"minecraft:block.note_block.pling MASTER 1.0 1.5\"",
            "#",
            "#     channel: ads",
            "#     rules:",
            "#       - type: PERMISSION",
            "#         permission: myplugin.staff",
            "#",
            "# You may add, remove, or edit entries as needed.",
            "# The dispatcher reads messages sequentially.",
            "#"
    })
    public List<ScheduledMessage> messages = List.of(
            ScheduledMessageBuilder.create()
                    .name("first-message")
                    .addNotices(
                            Notice.chat("<dark_gray>[<red>!<dark_gray>] <gray>This is the first announcement of <rainbow>AutoMessage<gray>!"),
                            Notice.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.MASTER, 1.0F, 1.0F)
                    )
                    .build(),

            ScheduledMessageBuilder.create()
                    .name("second-message-actionbar")
                    .addNotices(Notice.actionbar(
                            "<dark_gray>[<yellow>!<dark_gray>] <gray>This is the second announcement of <rainbow>AutoMessage<gray>!"
                    ))
                    .build(),

            ScheduledMessageBuilder.create()
                    .name("third-message-title")
                    .addNotices(Notice.title(
                            "<dark_gray>[<red>!<dark_gray>]",
                            "<rainbow>This is the third announcement!"
                    ))
                    .build(),

            ScheduledMessageBuilder.create()
                    .name("fourth-message-bossbar")
                    .addNotices(Notice.bossBar(
                            BossBar.Color.RED,
                            BossBar.Overlay.PROGRESS,
                            Duration.ofSeconds(5),
                            "<dark_gray>[<red><bold>!<dark_gray>] <rainbow>This is the fourth announcement!"
                    ))
                    .build(),

            ScheduledMessageBuilder.create()
                    .name("multiple-chat-actionbar")
                    .addNotices(
                            Notice.chat("<dark_gray>[<red>!<dark_gray>] <gray>This is a multi-channel announcement!"),
                            Notice.actionbar("<dark_gray>[<red>!<dark_gray>] <gray>This is a multi-channel announcement!")
                    )
                    .build(),

            ScheduledMessageBuilder.create()
                    .name("only-vip-permission")
                    .addNotice(
                            Notice.chat("<dark_gray>[<red>!<dark_gray>] <gray>This is a message to only players with `permission.vip`!")
                    )
                    .addRule(AudienceRule.permission("permission.vip"))
                    .build(),

            ScheduledMessageBuilder.create()
                    .name("only-vip-group")
                    .addNotice(
                            Notice.chat("<dark_gray>[<red>!<dark_gray>] <gray>This is a message to only players with VIP group!")
                    )
                    .addRule(AudienceRule.group("vip"))
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
