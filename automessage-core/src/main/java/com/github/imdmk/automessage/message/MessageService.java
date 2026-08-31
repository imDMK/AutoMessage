package com.github.imdmk.automessage.message;

import com.eternalcode.multification.adventure.AudienceConverter;
import com.eternalcode.multification.bukkit.BukkitMultification;
import com.eternalcode.multification.notice.provider.NoticeProvider;
import com.eternalcode.multification.translation.TranslationProvider;
import com.github.imdmk.automessage.language.LanguageConfig;
import com.github.imdmk.automessage.language.LanguageRegistry;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class MessageService extends BukkitMultification<LanguageConfig> {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final LanguageRegistry languages;
    private final AudienceProvider audienceProvider;

    public MessageService(
            LanguageRegistry languages,
            Plugin plugin
    ) {
        this.languages = languages;
        this.audienceProvider = BukkitAudiences.create(plugin);
    }

    /**
     * Multification hands the viewer's locale here, which is what lets two players in the same
     * chat read the same reply in different languages.
     */
    @Override
    protected TranslationProvider<LanguageConfig> translationProvider() {
        return languages::provide;
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

    public void send(CommandSender sender, NoticeProvider<LanguageConfig> notice) {
        create().viewer(sender).notice(notice).send();
    }

    public void shutdown() {
        audienceProvider.close();
    }
}
