package com.github.imdmk.automessage.feature.message.auto;

import com.eternalcode.multification.notice.Notice;
import com.eternalcode.multification.notice.resolver.NoticeResolverDefaults;
import com.eternalcode.multification.okaeri.MultificationSerdesPack;
import com.github.imdmk.automessage.configuration.ConfigSection;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.commons.SerdesCommons;
import net.kyori.adventure.bossbar.BossBar;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class AutoMessageConfiguration extends ConfigSection {

    @Comment("# How often should automatic messages be sent?")
    public Duration delay = Duration.ofSeconds(10);

    @Comment({
            "# Defines the selection strategy for automatic messages.",
            "# RANDOM - messages are chosen randomly.",
            "# SEQUENTIAL - messages are sent in order."
    })
    public AutoMessageMode mode = AutoMessageMode.SEQUENTIAL;

    @Comment({
            "# List of automatic messages to be dispatched.",
            "# Supports different Notice types like chat, actionbar, title, boss bar."
    })
    public List<Notice> messages = Arrays.asList(
            Notice.chat("<dark_gray>[<red>!<dark_gray>] <gray>This is first announcement of <rainbow>automessage <gray>plugin!"),
            Notice.actionbar("<dark_gray>[<yellow>!<dark_gray>] <gray>This is second announcement of <rainbow>automessage <gray>plugin!"),
            Notice.title("<dark_gray>[<red>!<dark_gray>]", "<rainbow>This is third announcement!"),
            Notice.bossBar(BossBar.Color.RED, BossBar.Overlay.PROGRESS, Duration.ofSeconds(5L), "<dark_gray>[<red><bold>!<dark_gray>] <rainbow>This is fourth announcement!")
    );

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> {
            registry.register(new SerdesCommons());
            registry.register(new MultificationSerdesPack(NoticeResolverDefaults.createRegistry()));
        };
    }

    @Override
    public @NotNull String getFileName() {
        return "autoMessageConfiguration.yml";
    }
}
