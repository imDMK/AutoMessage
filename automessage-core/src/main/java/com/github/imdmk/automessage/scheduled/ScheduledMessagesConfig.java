package com.github.imdmk.automessage.scheduled;

import com.eternalcode.multification.notice.Notice;
import com.eternalcode.multification.notice.resolver.NoticeResolverDefaults;
import com.eternalcode.multification.okaeri.MultificationSerdesPack;
import com.github.imdmk.automessage.config.ConfigSection;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRuleSerializer;
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
        "#  rules:     Optional audience restrictions.",
        "#             Supported rule types:",
        "#               • PERMISSION  -> Only players with this permission receive the message",
        "#               • GROUP        -> Only players in the given group receive the message",
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
            registry.register(new MultificationSerdesPack(NoticeResolverDefaults.createRegistry()));
        };
    }

    @Override
    public String getFileName() {
        return "scheduledMessages.yml";
    }
}
