package com.github.imdmk.automessage.platform.scheduler;

@FunctionalInterface
public interface TaskHandle {

    void cancel();

    static TaskHandle done() {
        return () -> { };
    }
}
