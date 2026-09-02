package com.github.imdmk.automessage.scheduled.trigger;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerCountMilestones {

    private final Set<Integer> announced = ConcurrentHashMap.newKeySet();

    // A threshold rearms only once the count has fallen back below it. Without that, a server
    // hovering on the boundary announces the same milestone every time one player logs in or out.
    public boolean reach(int threshold, int onlineCount) {
        if (onlineCount < threshold) {
            announced.remove(threshold);
            return false;
        }

        return announced.add(threshold);
    }
}
