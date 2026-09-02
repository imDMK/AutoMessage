package com.github.imdmk.automessage.notice;

import java.time.Duration;

@FunctionalInterface
public interface NoticeDelayer {

    void runLater(Duration delay, Runnable action);

    static NoticeDelayer immediate() {
        return (delay, action) -> action.run();
    }
}
