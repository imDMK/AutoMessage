package com.github.imdmk.automessage.shared.message;

import com.eternalcode.multification.adventure.AudienceConverter;
import com.eternalcode.multification.bukkit.BukkitMultification;
import com.eternalcode.multification.notice.provider.NoticeProvider;
import com.eternalcode.multification.translation.TranslationProvider;
import com.github.imdmk.automessage.shared.validate.Validator;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class MessageService extends BukkitMultification<MessageConfig> {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final MessageConfig messageConfig;
    private final AudienceProvider audienceProvider;

    public MessageService(
            @NotNull MessageConfig messageConfig,
            @NotNull AudienceProvider audienceProvider
    ) {
        this.messageConfig = Validator.notNull(messageConfig, "messageConfig cannot be null");
        this.audienceProvider = Validator.notNull(audienceProvider, "audienceProvider cannot be null");
    }

    /**
     * Returns a translation provider that always returns the same {@link MessageConfig} instance,
     * ignoring locale differences.
     *
     * @return locale-agnostic translation provider
     */
    @Override
    protected @NotNull TranslationProvider<MessageConfig> translationProvider() {
        return provider -> messageConfig;
    }

    /**
     * Returns the {@link MiniMessage}-based component serializer.
     *
     * @return component serializer for text serialization/deserialization
     */
    @Override
    protected @NotNull ComponentSerializer<Component, Component, String> serializer() {
        return MINI_MESSAGE;
    }

    /**
     * Converts Bukkit {@link CommandSender}s into Adventure audiences
     * using the configured {@link AudienceProvider}.
     *
     * <p>Players are mapped to player audiences, while other senders
     * (e.g., console or command blocks) are mapped to {@link AudienceProvider#console()}.</p>
     *
     * @return non-null audience converter
     */
    @Override
    protected @NotNull AudienceConverter<CommandSender> audienceConverter() {
        return sender -> {
            if (sender instanceof Player player) {
                return audienceProvider.player(player.getUniqueId());
            }
            return audienceProvider.console();
        };
    }

    /**
     * Sends a localized or static notice message to the specified Bukkit {@link CommandSender}.
     *
     * <p>The notice is resolved through the active {@link MessageConfig}
     * and rendered using {@link MiniMessage} formatting.</p>
     *
     * @param sender non-null Bukkit command sender (player, console, etc.)
     * @param notice non-null notice provider bound to {@link MessageConfig}
     * @throws NullPointerException if {@code sender} or {@code notice} is null
     */
    public void send(@NotNull CommandSender sender, @NotNull NoticeProvider<MessageConfig> notice) {
        Validator.notNull(sender, "sender cannot be null");
        Validator.notNull(notice, "notice cannot be null");
        create().viewer(sender).notice(notice).send();
    }
}
