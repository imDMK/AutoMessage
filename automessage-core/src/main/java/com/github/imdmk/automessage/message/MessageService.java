package com.github.imdmk.automessage.message;

import com.eternalcode.multification.adventure.AudienceConverter;
import com.eternalcode.multification.bukkit.BukkitMultification;
import com.eternalcode.multification.notice.provider.NoticeProvider;
import com.eternalcode.multification.translation.TranslationProvider;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class MessageService extends BukkitMultification<MessageConfig> {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final MessageConfigRegistry messages;
    private final AudienceProvider audienceProvider;

    public MessageService(
            MessageConfigRegistry messages,
            Plugin plugin
    ) {
        this.messages = messages;
        this.audienceProvider = BukkitAudiences.create(plugin);
    }

    /**
     * Multification passes the viewer's locale here, which is what lets two players sitting in the
     * same chat read the same reply in different languages.
     */
    @Override
    protected TranslationProvider<MessageConfig> translationProvider() {
        return messages::provide;
    }

    @Override
    protected ComponentSerializer<Component, Component, String> serializer() {
        return MINI_MESSAGE;
    }

    @Override
    protected AudienceConverter<CommandSender> audienceConverter() {
        return sender -> {
            if (sender instanceof Player player) {
                return audienceProvider.player(player.getUniqueId());
            }
            return audienceProvider.console();
        };
    }

    public void send(CommandSender sender, NoticeProvider<MessageConfig> notice) {
        create().viewer(sender).notice(notice).send();
    }

    public void shutdown() {
        audienceProvider.close();
    }
}
