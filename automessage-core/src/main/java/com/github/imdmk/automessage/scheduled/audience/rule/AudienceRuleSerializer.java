package com.github.imdmk.automessage.scheduled.audience.rule;

import com.github.imdmk.automessage.notice.time.DurationFormatter;
import com.github.imdmk.automessage.notice.time.DurationParser;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public final class AudienceRuleSerializer implements ObjectSerializer<AudienceRule> {

    @Override
    public boolean supports(@NotNull Class<? super AudienceRule> type) {
        return AudienceRule.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(AudienceRule rule, SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("type", rule.type(), AudienceRule.Type.class);

        switch (rule) {
            case AudienceGroupRule group ->
                    data.add("group", group.group(), String.class);

            case AudiencePermissionRule permission ->
                    data.add("permission", permission.permission(), String.class);

            case AudienceWorldRule world ->
                    data.addCollection("worlds", world.worlds(), String.class);

            case AudiencePlayerCountRule count -> {
                data.add("min", count.minimum(), Integer.class);

                // An open-ended range is the common case; writing Integer.MAX_VALUE into the file
                // would read as a configuration mistake rather than as "no upper bound".
                if (count.maximum() != AudiencePlayerCountRule.UNBOUNDED) {
                    data.add("max", count.maximum(), Integer.class);
                }
            }

            case AudiencePlayTimeRule playTime -> {
                data.add("min", DurationFormatter.format(playTime.minimum()), String.class);

                if (playTime.maximum() != null) {
                    data.add("max", DurationFormatter.format(playTime.maximum()), String.class);
                }
            }

            case AudienceAnyOfRule anyOf ->
                    data.addCollection("rules", anyOf.rules(), AudienceRule.class);

            case AudienceNoneOfRule noneOf ->
                    data.addCollection("rules", noneOf.rules(), AudienceRule.class);

            case AudienceNotRule not ->
                    data.add("rule", not.rule(), AudienceRule.class);
        }
    }

    @Override
    public AudienceRule deserialize(DeserializationData data, @NotNull GenericsDeclaration generics) {
        final AudienceRule.Type type = data.get("type", AudienceRule.Type.class);

        return switch (type) {
            case GROUP -> new AudienceGroupRule(data.get("group", String.class));
            case PERMISSION -> new AudiencePermissionRule(data.get("permission", String.class));

            case WORLD -> new AudienceWorldRule(Set.copyOf(data.getAsList("worlds", String.class)));

            case PLAYER_COUNT -> new AudiencePlayerCountRule(
                    data.containsKey("min") ? data.get("min", Integer.class) : 0,
                    data.containsKey("max")
                            ? data.get("max", Integer.class)
                            : AudiencePlayerCountRule.UNBOUNDED
            );

            case PLAYTIME -> new AudiencePlayTimeRule(
                    data.containsKey("min") ? duration(data.get("min", String.class)) : Duration.ZERO,
                    data.containsKey("max") ? duration(data.get("max", String.class)) : null
            );

            case ANY_OF -> new AudienceAnyOfRule(nested(data));
            case NONE_OF -> new AudienceNoneOfRule(nested(data));
            case NOT -> new AudienceNotRule(data.get("rule", AudienceRule.class));
        };
    }

    private static List<AudienceRule> nested(DeserializationData data) {
        return data.getAsList("rules", AudienceRule.class);
    }

    private static Duration duration(String value) {
        return DurationParser.parse(value);
    }
}
