package com.github.imdmk.automessage.language;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

/**
 * Everything the plugin says in reply to a command.
 *
 * <p>
 * One flat section rather than four nested ones: there are thirteen messages in total, and a
 * translator reading the file should not have to remember which subsection a string lives in.
 * </p>
 */
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
            "<dark_gray>• <green>Previewed scheduled message <gray>{MESSAGE}<green>.<dark_gray>"
    );

    public Notice messageNotFound = Notice.chat(
            "<dark_gray>• <red>No scheduled message named <gray>{MESSAGE} <red>exists in scheduledMessages.yml."
    );

    public Notice viewPlayerOnly = Notice.chat(
            "<dark_gray>• <red>Only players can preview scheduled messages.<dark_gray>"
    );
}
