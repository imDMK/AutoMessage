package com.github.imdmk.automessage.scheduled.placeholder;

import com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolver;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The placeholder tokens one message needs, resolved per viewer.
 *
 * <p>
 * A message is scanned once per broadcast rather than once per player — the tokens it contains do
 * not change between viewers, only their values do. On a message that uses no placeholders at all,
 * which is most of them, {@link #resolveFor} does no work and allocates nothing.
 * </p>
 *
 * <p>
 * Values are resolved on the thread that dispatches, which is the main thread. That matters for
 * PlaceholderAPI: expansions routinely read world and entity state, and resolving them on the
 * async delivery thread would be exactly the kind of off-thread API access this plugin avoids
 * elsewhere.
 * </p>
 */
public final class MessagePlaceholders {

    private static final MessagePlaceholders NONE =
            new MessagePlaceholders(Set.of(), Set.of(), ExternalPlaceholderResolver.disabled());

    private final Set<BuiltinPlaceholder> builtins;
    private final Set<String> externalTokens;
    private final ExternalPlaceholderResolver externalResolver;

    private MessagePlaceholders(
            Set<BuiltinPlaceholder> builtins,
            Set<String> externalTokens,
            ExternalPlaceholderResolver externalResolver
    ) {
        this.builtins = builtins;
        this.externalTokens = externalTokens;
        this.externalResolver = externalResolver;
    }

    public static MessagePlaceholders scan(
            ScheduledMessage message,
            ExternalPlaceholderResolver externalResolver
    ) {
        final Set<BuiltinPlaceholder> builtins = PlaceholderScanner.builtinsIn(message);
        final Set<String> externalTokens = externalResolver.available()
                ? PlaceholderScanner.externalTokensIn(message)
                : Set.of();

        if (builtins.isEmpty() && externalTokens.isEmpty()) {
            return NONE;
        }

        return new MessagePlaceholders(builtins, externalTokens, externalResolver);
    }

    public boolean isEmpty() {
        return builtins.isEmpty() && externalTokens.isEmpty();
    }

    @Unmodifiable
    public Map<String, String> resolveFor(Server server, Player viewer) {
        if (isEmpty()) {
            return Map.of();
        }

        final Map<String, String> resolved = new LinkedHashMap<>();

        for (final BuiltinPlaceholder builtin : builtins) {
            resolved.put(builtin.token(), builtin.resolve(server, viewer));
        }

        for (final String token : externalTokens) {
            resolved.put(token, externalResolver.resolve(viewer, token));
        }

        return Map.copyOf(resolved);
    }
}
