package com.github.imdmk.automessage.language;

import com.github.imdmk.automessage.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class CommandMessages extends OkaeriConfig {

    @Comment({"#", "# {PERMISSIONS} - the permissions the player is missing.", "#"})
    public Notice permissionMissing = Notice.chat(
            "<dark_gray>• <red>You are missing required permissions <gray>{PERMISSIONS} <red>to execute this command."
    );

    @Comment({"#", "# {USAGE} - the correct syntax.", "#"})
    public Notice usageInvalid = Notice.chat(
            "<dark_gray>• <red>Invalid command usage! <gray>Correct syntax: <red>{USAGE}<dark_gray>."
    );

    @Comment({"#", "# Shown above the list of usage variants when a command has several.", "#"})
    public Notice usageHeader = Notice.chat("<dark_gray>• <red>Correct usage variants:");

    @Comment({"#", "# One line of that list. {USAGE} - the variant.", "#"})
    public Notice usageEntry = Notice.chat("<dark_gray>• <red>{USAGE}");

    @Comment({"#", "# /automessage enable", "#"})
    public Notice dispatcherEnabled = Notice.chat(
            "<dark_gray>• <green>Automatic messages have been <bold>enabled</bold>.<dark_gray>"
    );

    public Notice dispatcherAlreadyEnabled = Notice.chat(
            "<dark_gray>• <yellow>Automatic messages are already enabled.<dark_gray>"
    );

    @Comment({"#", "# /automessage disable", "#"})
    public Notice dispatcherDisabled = Notice.chat(
            "<dark_gray>• <red>Automatic messages have been <bold>disabled</bold>.<dark_gray>"
    );

    public Notice dispatcherAlreadyDisabled = Notice.chat(
            "<dark_gray>• <yellow>Automatic messages are already disabled.<dark_gray>"
    );

    @Comment({"#", "# /automessage reload", "#"})
    public Notice configReloadedSuccess = Notice.chat(
            "<dark_gray>• <green>AutoMessage configuration has been reloaded successfully.<dark_gray>"
    );

    public Notice configReloadFailed = Notice.chat(
            "<dark_gray>• <red>Failed to reload AutoMessage configuration files. "
                    + "<red>Please disable the plugin and verify your configuration."
    );

    @Comment({"#", "# /automessage view. {MESSAGE} - the message name.", "#"})
    public Notice messagePreviewed = Notice.chat(
            "<dark_gray>• <green>Previewed scheduled message <yellow>{MESSAGE}<green>.<dark_gray>"
    );

    public Notice messageNotFound = Notice.chat(
            "<dark_gray>• <red>No scheduled message named <yellow>{MESSAGE} <red>exists in scheduledMessages.yml."
    );

    public Notice viewPlayerOnly = Notice.chat(
            "<dark_gray>• <red>Only players can preview scheduled messages.<dark_gray>"
    );

    @Comment({"#", "# /automessage next. {CHANNEL} - the channel, {MESSAGE} - the message name.", "#"})
    @Comment({"#", "# /automessage next. {CHANNEL}, {MESSAGE} and {DELAY} until it fires.", "#"})
    public Notice nextHeader = Notice.chat("<dark_gray>» <gradient:#4FC3F7:#0288D1><b>Next up on each channel</b></gradient>");

    public Notice nextEntry = Notice.chat(
            "<dark_gray>  • <aqua>{CHANNEL} <dark_gray>· <yellow>{MESSAGE} <dark_gray>· <gray>in <green>{DELAY}"
    );

    public Notice nextUnpredictable = Notice.chat(
            "<dark_gray>  • <aqua>{CHANNEL} <dark_gray>· <gray>drawn at random <dark_gray>· <gray>in <green>{DELAY}"
    );

    public Notice nextDisabled = Notice.chat(
            "<dark_gray>  • <aqua>{CHANNEL} <dark_gray>· <red>disabled in config.yml"
    );

    public Notice nextEmpty = Notice.chat(
            "<dark_gray>  • <aqua>{CHANNEL} <dark_gray>· <red>no messages assigned"
    );

    @Comment({
            "#",
            "# /automessage stats. {TOTAL}, {CHANNEL}, {MESSAGE}, {COUNT} and {AGO}.",
            "# Channels are listed apart from messages, so a name is never ambiguous: a channel",
            "# row says when the next one is due, a message row names the channel it goes out on.",
            "#"
    })
    public Notice statsHeader = Notice.chat(
            "<dark_gray>» <gradient:#A78BFA:#45D7E8><b>AutoMessage</b></gradient> <dark_gray>· <gray>sent <white><b>{TOTAL}</b> <gray>since startup"
    );

    public Notice statsEmpty = Notice.chat("<dark_gray>» <gradient:#A78BFA:#45D7E8><b>AutoMessage</b></gradient> <dark_gray>· <gray>nothing announced yet");

    public Notice statsChannelsHeader = Notice.chat("<dark_gray>» <gradient:#4FC3F7:#0288D1><b>Channels</b></gradient>");

    public Notice statsChannel = Notice.chat(
            "<dark_gray>  • <aqua>{CHANNEL} <dark_gray>· <white><b>{COUNT}x</b> <dark_gray>· <gray>next in <green>{DELAY}"
    );

    public Notice statsChannelPending = Notice.chat(
            "<dark_gray>  • <aqua>{CHANNEL} <dark_gray>· <gray>nothing yet <dark_gray>· <gray>next in <green>{DELAY}"
    );

    public Notice statsChannelDisabled = Notice.chat(
            "<dark_gray>  • <aqua>{CHANNEL} <dark_gray>· <white><b>{COUNT}x</b> <dark_gray>· <gray>"
                    + "<red>disabled in config.yml"
    );

    public Notice statsChannelEmpty = Notice.chat(
            "<dark_gray>  • <aqua>{CHANNEL} <dark_gray>· <white><b>{COUNT}x</b> <dark_gray>· <gray>"
                    + "<red>no messages assigned"
    );

    public Notice statsMessagesHeader = Notice.chat("<dark_gray>» <gradient:#FFD54F:#FFA000><b>Messages</b></gradient>");

    public Notice statsEntry = Notice.chat(
            "<dark_gray>  • <yellow>{MESSAGE} <dark_gray>(<aqua>{CHANNEL}<dark_gray>) · "
                    + "<white><b>{COUNT}x</b> <dark_gray>· <green>{AGO} <gray>ago"
    );

    @Comment({"#", "# /automessage send. {CHANNEL}, {MESSAGE} and {DELAY} until the next one.", "#"})
    public Notice sendDone = Notice.chat(
            "<dark_gray>• <green>Sent <yellow>{MESSAGE} <green>on <aqua>{CHANNEL}<green>. "
                    + "Next one in <gray>{DELAY}<green>.<dark_gray>"
    );

    public Notice sendNobodyOnline = Notice.chat(
            "<dark_gray>• <yellow>Nobody is online, so nothing was sent on <aqua>{CHANNEL}<yellow>. "
                    + "Its schedule was left alone.<dark_gray>"
    );

    public Notice sendNoMessages = Notice.chat(
            "<dark_gray>• <red>Channel <aqua>{CHANNEL} <red>has no messages assigned to it.<dark_gray>"
    );

    public Notice sendDisabled = Notice.chat(
            "<dark_gray>• <red>Channel <aqua>{CHANNEL} <red>is disabled in config.yml.<dark_gray>"
    );

    @Comment({"#", "# /automessage toggle. A player turning announcements off for themselves.", "#"})
    public Notice toggleOff = Notice.chat(
            "<dark_gray>• <gray>Announcements are now <red>off <gray>for you"
    );

    public Notice toggleOn = Notice.chat(
            "<dark_gray>• <gray>Announcements are <green>on <gray>for you again"
    );

    public Notice togglePlayerOnly = Notice.chat(
            "<dark_gray>• <red>Only players can turn announcements off"
    );

    public Notice channelNotFound = Notice.chat(
            "<dark_gray>• <red>No channel named <aqua>{CHANNEL} <red>exists in config.yml."
    );
}
