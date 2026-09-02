package com.github.imdmk.automessage.scheduled;

import com.github.imdmk.automessage.config.ConfigSection;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRuleSerializer;
import com.github.imdmk.automessage.scheduled.trigger.MessageTrigger;
import com.github.imdmk.automessage.scheduled.trigger.MessageTriggerSerializer;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import com.github.imdmk.automessage.platform.capability.Capabilities;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;

import java.time.Duration;
import java.util.List;

@Header({
        "# ============================================================================",
        "#                        AutoMessage - scheduledMessages.yml",
        "# ============================================================================",
        "# WHEN each announcement is sent and WHO receives it.",
        "#",
        "# What it SAYS lives in the language files, under 'announcements', keyed by",
        "# the same name: lang/en.yml, lang/pl.yml, and any you add yourself.",
        "#",
        "# The smallest possible entry - sent to everyone, on the default schedule:",
        "#",
        "#   - name: vote-reminder",
        "#",
        "# and in lang/en.yml:",
        "#",
        "#   announcements:",
        "#     vote-reminder:",
        "#       - \"<gray>Vote for the server and claim your reward!\"",
        "#",
        "# Splitting the two means adding a language never touches this file, and",
        "# adding an announcement never means editing every language at once.",
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
        "# ============================================================================"
})
public final class ScheduledMessagesConfig extends ConfigSection {

    @Comment({
            "#",
            "# REQUIRED",
            "#",
            "#   name      Ties this entry to its text in the language files, and is what",
            "#             /automessage view takes.",
            "#",
            "# OPTIONAL",
            "#",
            "#   channel   Which stream in config.yml sends it. Omitted means 'default'.",
            "#",
            "#               channel: ads",
            "#",
            "#   weight    How often the WEIGHTED selector picks it, relative to the",
            "#             others. Default 1. Use 0 to park a message without deleting it.",
            "#",
            "#               weight: 5",
            "#",
            "#   rules     Who receives it. All rules must pass. Omit to send to everyone.",
            "#",
            "#               rules:",
            "@requires PERMISSION_RULE",
            "#                 - type: PERMISSION",
            "#                   permission: rank.vip",
            "@end",
            "@requires GROUP_RULE",
            "#                 - type: GROUP",
            "#                   group: vip",
            "@end",
            "@requires WORLD_RULE",
            "#                 - type: WORLD",
            "#                   worlds: [world, world_nether]",
            "@end",
            "#                 - type: PLAYER_COUNT     # while 1-10 players are online",
            "#                   min: 1",
            "#                   max: 10",
            "@requires PLAYTIME_RULE",
            "#                 - type: PLAYTIME         # players with under 2h played",
            "#                   max: 2h",
            "@end",
            "#",
            "#             ANY_OF, NONE_OF and NOT nest the rules above - use them for",
            "#             anything AND cannot say, such as \"VIP or moderator\":",
            "#",
            "@requires PERMISSION_RULE",
            "#               rules:",
            "#                 - type: ANY_OF",
            "#                   rules:",
            "#                     - type: PERMISSION",
            "#                       permission: rank.vip",
            "#                     - type: PERMISSION",
            "#                       permission: rank.mod",
            "#",
            "@end",
            "#   trigger   Sends it on an event instead of on the timetable. A message",
            "#             with a trigger leaves the rotation entirely; rules still apply.",
            "#",
            "#               trigger:",
            "#                 type: JOIN",
            "#                 delay: 3s             # let the join spam settle first",
            "#",
            "@requires FIRST_JOIN_TRIGGER",
            "#               trigger:",
            "#                 type: FIRST_JOIN      # a player's very first connection",
            "#                 delay: 3s",
            "#",
            "@end",
            "#               trigger:",
            "#                 type: PLAYER_COUNT    # fires once on reaching 100 online",
            "#                 threshold: 100",
            "#",
            "# PLACEHOLDERS - write these in the text, in the language files",
            "#",
            "#   {PLAYER} {DISPLAY_NAME} {UUID}              about the reader",
            "@requires WORLD_RULE",
            "#   {WORLD}                                     the world they are in",
            "@end",
            "#   {ONLINE} {MAX_PLAYERS} {DATE} {TIME}        about the server",
            "#",
            "@requires EXTERNAL_PLACEHOLDERS",
            "#   With PlaceholderAPI installed, %any_placeholder% works too.",
            "#",
            "@end"
    })
    public List<ScheduledMessage> messages = List.of(
            ScheduledMessageBuilder.create().name("vote-reminder").build(),
            ScheduledMessageBuilder.create().name("discord-invite").build(),
            ScheduledMessageBuilder.create().name("server-status").build(),
            ScheduledMessageBuilder.create().name("event-announcement").build(),
            ScheduledMessageBuilder.create().name("restart-warning").build(),
            ScheduledMessageBuilder.create().name("shop-advert").build(),

            // Only players holding the permission see this one.
            ScheduledMessageBuilder.create()
                    .name("vip-perk-reminder")
                    .addRule(AudienceRule.permission("rank.vip"))
                    .build(),

            // PLAYTIME reads the server's own statistic, so it needs no other plugin.
            ScheduledMessageBuilder.create()
                    .name("newcomer-tip")
                    .addRule(AudienceRule.playTime(Duration.ZERO, Duration.ofHours(2)))
                    .build(),

            // Not part of the rotation: fires when a player joins for the first time.
            ScheduledMessageBuilder.create()
                    .name("first-join-welcome")
                    .trigger(MessageTrigger.firstJoin(Duration.ofSeconds(3)))
                    .build()
    );

    @Override
    public void applyCapabilities(Capabilities capabilities) {
        this.messages = this.messages.stream()
                .filter(message -> CapabilityRequirements.satisfiedBy(message, capabilities))
                .toList();
    }

    @Override
    public OkaeriSerdesPack getSerdesPack() {
        return registry -> {
            registry.register(new ScheduledMessageSerializer());
            registry.register(new AudienceRuleSerializer());
            registry.register(new MessageTriggerSerializer());
        };
    }

    @Override
    public String getFileName() {
        return "scheduledMessages.yml";
    }
}
