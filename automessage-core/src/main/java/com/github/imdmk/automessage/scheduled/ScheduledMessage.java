package com.github.imdmk.automessage.scheduled;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceRule;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import com.github.imdmk.automessage.scheduled.locale.MessageLocale;
import com.github.imdmk.automessage.scheduled.locale.MessageTranslation;
import com.github.imdmk.automessage.scheduled.trigger.MessageTrigger;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

public record ScheduledMessage(
        String name,
        @Unmodifiable List<Notice> notices,
        @Unmodifiable List<AudienceRule> rules,
        int weight,
        String channel,
        @Nullable MessageTrigger trigger,
        @Unmodifiable List<MessageTranslation> translations) {

    /**
     * Relative weight of a message that does not configure one.
     *
     * <p>
     * Every message sharing the default is what makes {@code WEIGHTED} behave exactly like
     * {@code RANDOM} until somebody actually tunes a weight.
     * </p>
     */
    public static final int DEFAULT_WEIGHT = 1;

    public ScheduledMessage {
        Objects.requireNonNull(name, "name");

        if (notices.isEmpty()) {
            throw new IllegalArgumentException("notices must contains at least one notice");
        }

        if (weight < 0) {
            throw new IllegalArgumentException("weight must not be negative, got " + weight);
        }

        // Normalised once here so the dispatcher can group messages by a plain string equality
        // check instead of case-folding every message on every tick.
        channel = AnnouncementChannel.normalize(channel);

        notices = List.copyOf(notices);
        rules = List.copyOf(rules);
        translations = List.copyOf(translations);
    }

    /**
     * A message that configures nothing weighs the default, joins the default channel, takes part
     * in the timed rotation and reads the same for everyone.
     */
    public ScheduledMessage(
            String name,
            List<Notice> notices,
            List<AudienceRule> rules
    ) {
        this(name, notices, rules, DEFAULT_WEIGHT, AnnouncementChannel.DEFAULT_NAME, null, List.of());
    }

    public boolean belongsTo(AnnouncementChannel announcementChannel) {
        return announcementChannel.matches(channel);
    }

    /**
     * A triggered message fires on its event and never through the rotation; leaving it in both
     * would announce it at random moments as well as the one it was written for.
     */
    public boolean isScheduled() {
        return trigger == null;
    }

    /**
     * Picks the notices a player of this locale should receive.
     *
     * <p>
     * An exact match wins over a language-only one, so {@code pt_br} is preferred to {@code pt}
     * for a Brazilian client while a Portuguese one still falls back to {@code pt}. A player whose
     * language nobody translated gets the default notices rather than nothing at all.
     * </p>
     *
     * @param locale locale reported by the client, e.g. {@code pl_pl}
     * @return the notices to send
     */
    @Unmodifiable
    public List<Notice> noticesFor(String locale) {
        if (translations.isEmpty()) {
            return notices;
        }

        final String normalized = MessageLocale.normalize(locale);

        for (final MessageTranslation translation : translations) {
            if (translation.locale().equals(normalized)) {
                return translation.notices();
            }
        }

        final String language = MessageLocale.language(locale);

        for (final MessageTranslation translation : translations) {
            if (translation.locale().equals(language)) {
                return translation.notices();
            }
        }

        return notices;
    }
}
