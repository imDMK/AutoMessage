package com.github.imdmk.automessage.feature.message;

import com.eternalcode.multification.notice.Notice;
import com.eternalcode.multification.notice.resolver.NoticeResolverDefaults;
import com.eternalcode.multification.okaeri.MultificationSerdesPack;
import com.github.imdmk.automessage.configuration.ConfigSection;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

public class MessageConfiguration extends ConfigSection {

    @Comment({
            "# Sent when the automatic message delay is changed",
            "# {DELAY} - New delay value"
    })
    public Notice autoMessageDelayChange = Notice.chat("<green>Changed auto message delay to {DELAY}.");

    @Comment({
            "# Sent when querying the current automatic message delay",
            "# {DELAY} - Current delay value"
    })
    public Notice autoMessageDelay = Notice.chat("<green>Current auto message delay is {DELAY}.");

    @Comment({
            "# Sent when a command is used incorrectly",
            "# {USAGE} - Correct command usage"
    })
    public Notice invalidCommandUsage = Notice.chat("<red>Invalid usage<dark_gray>: <red>{USAGE}");

    @Comment("# Header for multiple valid usages of a command")
    public Notice usageHeader = Notice.chat("<red>Invalid usage<dark_gray>:");

    @Comment({
            "# Entry in the list of valid usages",
            "# {USAGE} - Correct command usage"
    })
    public Notice usageEntry = Notice.chat("<dark_gray>- <red>{USAGE}");

    @Comment({
            "# Sent when command sender lacks required permissions",
            "# {PERMISSIONS} - Required permission nodes"
    })
    public Notice missingPermissions = Notice.chat("<red>Missing permissions<dark_gray>: <red>{PERMISSIONS}");

    @Comment("# Sent when the player could not be found")
    public Notice playerNotFound = Notice.chat("<red>Player not found<dark_gray>.");

    @Comment("# Sent when successfully reloaded all plugin configuration files")
    public Notice reload = Notice.chat("<green>The plugin configuration files has been reloaded. May note that not all functions are reloaded.");

    @Comment("# Sent when there is an error loading plugin configuration files")
    public Notice reloadError = Notice.chat("<red>Failed to reload plugin configuration files. Please see the console.");

    @Override
    public @NotNull OkaeriSerdesPack getSerdesPack() {
        return registry -> {
            registry.register(new MultificationSerdesPack(NoticeResolverDefaults.createRegistry()));
        };
    }

    @Override
    public @NotNull String getFileName() {
        return "messageConfiguration.yml";
    }
}
