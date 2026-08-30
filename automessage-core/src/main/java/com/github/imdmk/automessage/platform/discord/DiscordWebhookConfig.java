package com.github.imdmk.automessage.platform.discord;

import com.github.imdmk.automessage.config.ConfigSection;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;

@Header({
        "# ============================================================================",
        "#                        AutoMessage — discordWebhook.yml",
        "# ============================================================================",
        "# Mirrors announcements to a Discord channel through a webhook, so the players",
        "# who are not currently on the server still see them.",
        "#",
        "# Disabled by default, and nothing is ever sent while 'url' is empty - the",
        "# plugin makes no outbound requests unless you configure one here.",
        "#",
        "# Creating a webhook:",
        "#   Discord -> Server Settings -> Integrations -> Webhooks -> New Webhook",
        "#   Copy the webhook URL and paste it below.",
        "#",
        "# Treat that URL as a password: anyone holding it can post to your channel.",
        "#",
        "# Only the chat part of a message is mirrored. Titles, action bars, boss bars",
        "# and sounds have no meaning in Discord and are skipped, and MiniMessage tags",
        "# are rendered down to plain text.",
        "#",
        "# Source Code:",
        "#   https://github.com/imDMK/AutoMessage",
        "#",
        "# Support development:",
        "#   PayPal: https://paypal.me/dominiksuliga",
        "#",
        "# ============================================================================"
})
public final class DiscordWebhookConfig extends ConfigSection {

    @Comment({"#", "# Whether announcements are mirrored to Discord.", "#"})
    public boolean enabled = false;

    @Comment({
            "#",
            "# Webhook URL taken from your Discord channel settings.",
            "# Must point at discord.com; anything else is rejected on startup so a",
            "# mistyped URL cannot quietly send your announcements to a stranger.",
            "#"
    })
    public String url = "";

    @Comment({"#", "# Name the webhook posts under. Leave empty to use Discord's own.", "#"})
    public String username = "AutoMessage";

    @Comment({
            "#",
            "# Avatar the webhook posts with. Leave empty to use Discord's own.",
            "#"
    })
    public String avatarUrl = "";

    @Override
    public OkaeriSerdesPack getSerdesPack() {
        return registry -> {};
    }

    @Override
    public String getFileName() {
        return "discordWebhook.yml";
    }
}
