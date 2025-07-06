package com.github.imdmk.automessage.feature.message;

import com.eternalcode.multification.notice.Notice;
import com.eternalcode.multification.notice.resolver.NoticeResolverDefaults;
import com.eternalcode.multification.okaeri.MultificationSerdesPack;
import com.github.imdmk.automessage.configuration.ConfigSection;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import org.jetbrains.annotations.NotNull;

public class MessageConfig extends ConfigSection {

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

    @Comment("# Shown when auto messages are successfully enabled.")
    public Notice autoMessageEnable = Notice.chat("<green>Enabled auto messages!");

    @Comment("# Shown when auto messages are already enabled.")
    public Notice autoMessageAlreadyEnabled = Notice.chat("<red>Auto messages are currently enabled!");

    @Comment("# Shown when auto messages are successfully disabled.")
    public Notice autoMessageDisable = Notice.chat("<green>Disabled auto messages!");

    @Comment("# Sent when automatic messages are already disabled")
    public Notice autoMessageAlreadyDisabled = Notice.chat("<red>Auto messages are already disabled!");

    @Comment("# Sent when a random automatic message is dispatched")
    public Notice autoMessageRandomDispatched = Notice.chat("<green>Dispatched a random auto message.");

    @Comment("# Sent when a selected automatic message is dispatched")
    public Notice autoMessageSelectedDispatched = Notice.chat("<green>Dispatched a selected auto message.");

    @Comment({
            "# Sent when the selected target player cannot receive the selected message",
            "# Typically due to insufficient group or permission as defined in the configuration"
    })
    public Notice autoMessageSelectedCannotReceive = Notice.chat("<red>Selected player cannot receive the selected auto message (e.g. missing required group or permission).");

    @Comment("# Sent when the provided auto message name does not exist")
    public Notice autoMessageNotFound = Notice.chat("<red>Auto message with the given name not found.");

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
        return "messageConfig.yml";
    }
}
