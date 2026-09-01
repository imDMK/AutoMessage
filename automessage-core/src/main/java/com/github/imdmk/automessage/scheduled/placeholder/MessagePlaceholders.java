package com.github.imdmk.automessage.scheduled.placeholder;

import com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolver;
import com.github.imdmk.automessage.notice.Notice;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;
import org.jetbrains.annotations.Unmodifiable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            List<List<Notice>> translations,
            ExternalPlaceholderResolver externalResolver
    ) {
        final Set<BuiltinPlaceholder> builtins = PlaceholderScanner.builtinsIn(translations);
        final Set<String> externalTokens = externalResolver.available()
                ? PlaceholderScanner.externalTokensIn(translations)
                : Set.of();

        if (builtins.isEmpty() && externalTokens.isEmpty()) {
            return NONE;
        }

        return new MessagePlaceholders(builtins, externalTokens, externalResolver);
    }

    // Whether the rendered text can differ between two players. A PlaceholderAPI token is
    // assumed to, since only the expansion behind it knows, and guessing wrong would send one
    // player another player's text.
    public boolean viewerScoped() {
        if (!externalTokens.isEmpty()) {
            return true;
        }
        for (final BuiltinPlaceholder builtin : builtins) {
            if (builtin.requiresViewer()) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return builtins.isEmpty() && externalTokens.isEmpty();
    }

    @Unmodifiable
    public Map<String, String> resolveWithoutViewer(ViewerRegistry viewers) {
        if (isEmpty()) {
            return Map.of();
        }

        final Map<String, String> resolved = new LinkedHashMap<>();

        for (final BuiltinPlaceholder builtin : builtins) {
            resolved.put(
                    builtin.token(),
                    builtin.requiresViewer() ? "" : builtin.resolveForServer(viewers)
            );
        }

        for (final String token : externalTokens) {
            final String value = externalResolver.resolveWithoutViewer(token);

            // An expansion that needs a player hands the token straight back; showing it raw
            // would be the same broken output, so it is dropped as well.
            resolved.put(token, value.equals(token) ? "" : value);
        }

        return Map.copyOf(resolved);
    }

    @Unmodifiable
    public Map<String, String> resolveFor(ViewerRegistry viewers, Viewer viewer) {
        if (isEmpty()) {
            return Map.of();
        }

        final Map<String, String> resolved = new LinkedHashMap<>();

        for (final BuiltinPlaceholder builtin : builtins) {
            resolved.put(builtin.token(), builtin.resolve(viewers, viewer));
        }

        for (final String token : externalTokens) {
            resolved.put(token, externalResolver.resolve(viewer, token));
        }

        return Map.copyOf(resolved);
    }
}
