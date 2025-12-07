package com.github.imdmk.automessage.scheduled.audience.rule;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.jetbrains.annotations.NotNull;

public final class AudienceRuleSerializer implements ObjectSerializer<AudienceRule> {

    @Override
    public boolean supports(@NotNull Class<? super AudienceRule> type) {
        return AudienceRule.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NotNull AudienceRule rule, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("type", rule.type(), AudienceRule.Type.class);

        switch (rule) {
            case AudienceGroupRule audienceGroupRule -> data.add("group", audienceGroupRule.group(), String.class);
            case AudiencePermissionRule audiencePermissionRule -> data.add("permission", audiencePermissionRule.permission(), String.class);
        }
    }

    @Override
    public AudienceRule deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        final AudienceRule.Type type = data.get("type", AudienceRule.Type.class);

        return switch (type) {
            case GROUP -> new AudienceGroupRule(data.get("group", String.class));
            case PERMISSION -> new AudiencePermissionRule(data.get("permission", String.class));
        };
    }
}
