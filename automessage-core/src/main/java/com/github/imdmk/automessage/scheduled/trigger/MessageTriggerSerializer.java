package com.github.imdmk.automessage.scheduled.trigger;

import com.github.imdmk.automessage.notice.time.DurationFormatter;
import com.github.imdmk.automessage.notice.time.DurationParser;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public final class MessageTriggerSerializer implements ObjectSerializer<MessageTrigger> {

    @Override
    public boolean supports(@NotNull Class<? super MessageTrigger> type) {
        return MessageTrigger.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(MessageTrigger trigger, SerializationData data, @NotNull GenericsDeclaration generics) {
        data.add("type", trigger.type(), MessageTrigger.Type.class);

        switch (trigger) {
            case JoinTrigger join -> {
                if (!join.delay().isZero()) {
                    data.add("delay", DurationFormatter.format(join.delay()), String.class);
                }
            }
            case PlayerCountTrigger count -> data.add("threshold", count.threshold(), Integer.class);
        }
    }

    @Override
    public MessageTrigger deserialize(DeserializationData data, @NotNull GenericsDeclaration generics) {
        // Both required fields are checked by hand. Left to okaeri they surface as a
        // NullPointerException naming its own internals, which tells an administrator editing
        // YAML nothing about which line is wrong.
        if (!data.containsKey("type")) {
            throw new IllegalArgumentException(
                    "a trigger needs a 'type': one of " + java.util.Arrays.toString(MessageTrigger.Type.values())
            );
        }

        final MessageTrigger.Type type = data.get("type", MessageTrigger.Type.class);

        return switch (type) {
            case JOIN -> new JoinTrigger(delayOf(data), false);
            case FIRST_JOIN -> new JoinTrigger(delayOf(data), true);
            case PLAYER_COUNT -> new PlayerCountTrigger(thresholdOf(data));
        };
    }

    private static Duration delayOf(DeserializationData data) {
        return data.containsKey("delay")
                ? DurationParser.parse(data.get("delay", String.class))
                : Duration.ZERO;
    }

    private static int thresholdOf(DeserializationData data) {
        if (!data.containsKey("threshold")) {
            throw new IllegalArgumentException(
                    "a PLAYER_COUNT trigger needs a 'threshold': the online count that fires it"
            );
        }

        return data.get("threshold", Integer.class);
    }
}
