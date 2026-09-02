package com.github.imdmk.automessage.platform;

import com.github.imdmk.automessage.platform.capability.Capabilities;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import com.github.imdmk.automessage.platform.viewer.PlaytimeSource;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;

public interface Platform {

    String name();

    Capabilities capabilities();

    ViewerRegistry viewers();

    TaskScheduler scheduler();

    PlaytimeSource playtime();
}
