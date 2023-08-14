package com.github.imdmk.automessage.scheduler;

public interface TaskScheduler {

    void runAsync(Runnable runnable);

    void runLaterAsync(Runnable runnable, long delay);

    void runTimerAsync(Runnable runnable, long delay, long period);
}
