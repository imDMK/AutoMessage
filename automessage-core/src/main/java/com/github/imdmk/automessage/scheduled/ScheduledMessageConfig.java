package com.github.imdmk.automessage.scheduled;

import com.eternalcode.multification.notice.Notice;
import com.eternalcode.multification.notice.resolver.NoticeResolverDefaults;
import com.eternalcode.multification.okaeri.MultificationSerdesPack;
import com.github.imdmk.automessage.config.ConfigSection;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRuleSerializer;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public final class ScheduledMessageConfig extends ConfigSection {

    @Comment({
            "#",
            "# List of all scheduled messages dispatched automatically by the plugin.",
            "# Each entry contains:",
            "#   - name: unique identifier",
            "#   - notices: one or more message types (chat, title, actionbar, bossbar, sound)",
            "#   - rules: audience restrictions (permissions, groups)",
            "#",
            "# You may freely remove, modify, or add new message entries.",
            "# Message order is preserved and processed sequentially by the dispatcher.",
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
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> {
            registry.register(new ScheduledMessageSerializer());
            registry.register(new AudienceRuleSerializer());
            registry.register(new MultificationSerdesPack(NoticeResolverDefaults.createRegistry()));
        };
    }

    @Override
    public @NotNull String getFileName() {
        return "scheduledMessages.yml";
    }
}
