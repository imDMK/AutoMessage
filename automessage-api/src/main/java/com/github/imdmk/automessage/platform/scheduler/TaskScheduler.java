package com.github.imdmk.automessage.platform.scheduler;

import java.time.Duration;

// Deliberately carries only what something actually calls. Every method here is implemented once
// per platform, on threading models that differ enough to be hard - Folia has no global scheduler
// and a proxy has no game thread - so an unused method is not free, it is six untested ones.
public interface TaskScheduler {

    TaskHandle runAsync(Runnable runnable);

    TaskHandle runLaterSync(Runnable runnable, Duration delay);

    TaskHandle runTimerSync(PluginTask task);

    void shutdown();
}
