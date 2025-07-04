package com.github.imdmk.automessage.feature.message.auto;

import com.eternalcode.multification.notice.Notice;
import com.eternalcode.multification.notice.resolver.NoticeResolverDefaults;
import com.eternalcode.multification.okaeri.MultificationSerdesPack;
import com.github.imdmk.automessage.configuration.ConfigSection;
import com.github.imdmk.automessage.feature.message.auto.selector.AutoMessageSelectorMode;
import com.github.imdmk.automessage.feature.message.auto.sound.AutoMessageSerializer;
import com.github.imdmk.automessage.feature.message.auto.sound.AutoMessageSound;
import com.github.imdmk.automessage.feature.message.auto.sound.SoundSerializer;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AutoMessageConfig extends ConfigSection {

    @Comment("# How often should automatic messages be sent?")
    public Duration delay = Duration.ofSeconds(10);

    @Comment({
            "# Defines the selection strategy for automatic messages.",
            "# RANDOM - messages are chosen randomly.",
            "# SEQUENTIAL - messages are sent in order."
    })
    public AutoMessageSelectorMode mode = AutoMessageSelectorMode.SEQUENTIAL;

    @Comment({
            "# List of automatic messages to be dispatched.",
            "# Supports different Notice types like chat, actionbar, title, boss bar.",
            "# To make a new line in chat message, use '\n'"
    })
    public List<AutoMessageNotice> messages = Arrays.asList(
            AutoMessageNotice.builder()
                    .notice(Notice.chat("<dark_gray>[<red>!<dark_gray>] <gray>This is first announcement of <rainbow>automessage <gray>plugin!"))
                    .sound(new AutoMessageSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1))
                    .build(),

            AutoMessageNotice.builder()
                    .notice(Notice.actionbar("<dark_gray>[<yellow>!<dark_gray>] <gray>This is second announcement of <rainbow>automessage <gray>plugin!"))
                    .build(),

            AutoMessageNotice.builder()
                    .notice(Notice.title("<dark_gray>[<red>!<dark_gray>]", "<rainbow>This is third announcement!"))
                    .build(),

            AutoMessageNotice.builder()
                    .notice(Notice.bossBar(BossBar.Color.RED, BossBar.Overlay.PROGRESS, Duration.ofSeconds(5L), "<dark_gray>[<red><bold>!<dark_gray>] <rainbow>This is fourth announcement!"))
                    .sound(new AutoMessageSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1))
                    .build(),

            AutoMessageNotice.builder()
                    .notices(List.of(
                            Notice.chat("<dark_gray>[<red>!<dark_gray>] <gray>This is multiple announcements!"),
                            Notice.actionbar("<dark_gray>[<red>!<dark_gray>] <gray>This is multiple announcements!")
                    ))
                    .build(),

            AutoMessageNotice.builder()
                    .notice(Notice.chat("<dark_gray>[<red>!<dark_gray>] <gray>This a announcement only for players with 'vip' permission!"))
                    .requiredPermission("vip")
                    .build(),

            AutoMessageNotice.builder()
                    .notice(Notice.chat("<dark_gray>[<red>!<dark_gray>] <gray>This a announcement only for players with 'vip' group!"))
                    .requiredGroup("vip")
                    .build()
    );

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> {
            registry.register(new SerdesCommons());
            registry.register(new AutoMessageNoticeSerializer());
            registry.register(new MultificationSerdesPack(NoticeResolverDefaults.createRegistry()));

            registry.register(new AutoMessageSerializer());
            registry.register(new SoundSerializer());
        };
    }

    @Override
    public @NotNull String getFileName() {
        return "autoMessageConfig.yml";
    }

    public void setDelay(@NotNull Duration delay) {
        Objects.requireNonNull( delay, "delay cannot be null");
        this.delay = delay;
    }
}
