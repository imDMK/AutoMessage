package com.github.imdmk.automessage.command.view.messages;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public final class ENViewMessages
        extends OkaeriConfig
        implements ViewMessages {

    @Comment({
            "#",
            "# Confirmation sent after a scheduled message has been previewed.",
            "#",
            "# Placeholders:",
            "#  {MESSAGE} - name of the previewed message.",
            "#"
    })
    Notice messagePreviewed = Notice.chat(
            "<dark_gray>• <green>Previewed scheduled message <gray>{MESSAGE}<green>.<dark_gray>"
    );

    @Comment({
            "#",
            "# Sent when no scheduled message with the given name exists in scheduledMessages.yml.",
            "#",
            "# Placeholders:",
            "#  {MESSAGE} - name that was typed.",
            "#"
    })
    Notice messageNotFound = Notice.chat(
            "<dark_gray>• <red>No scheduled message named <gray>{MESSAGE} <red>exists in scheduledMessages.yml."
    );

    @Comment({
            "#",
            "# Sent when the console tries to preview a message.",
            "# Previews are rendered in-game, so they require a player.",
            "#"
    })
    Notice viewPlayerOnly = Notice.chat(
            "<dark_gray>• <red>Only players can preview scheduled messages.<dark_gray>"
    );

    @Override
    public Notice messagePreviewed() {
        return messagePreviewed;
    }

    @Override
    public Notice messageNotFound() {
        return messageNotFound;
    }

    @Override
    public Notice viewPlayerOnly() {
        return viewPlayerOnly;
    }
}
