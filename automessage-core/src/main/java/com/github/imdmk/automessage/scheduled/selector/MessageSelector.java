package com.github.imdmk.automessage.scheduled.selector;

import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public interface MessageSelector {

    Optional<ScheduledMessage> selectNext(@NotNull List<ScheduledMessage> messages, boolean advanceIndex);

    default Optional<ScheduledMessage> selectNext(@NotNull List<ScheduledMessage> messages) {
        return selectNext(messages, true);
    }
}
