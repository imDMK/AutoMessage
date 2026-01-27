package com.github.imdmk.automessage.platform.scheduler;

import java.time.Duration;

public interface PluginTask extends Runnable {

    @Override
    void run();

    Duration delay();
    Duration period();

}
