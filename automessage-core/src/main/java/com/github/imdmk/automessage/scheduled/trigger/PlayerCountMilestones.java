package com.github.imdmk.automessage.scheduled.trigger;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Remembers which player-count thresholds have already been announced.
 *
 * <p>
 * A threshold fires when the count crosses it going up, and rearms only once the count has fallen
 * back below. Without that, a server hovering on the boundary would announce the same milestone
 * every time a single player logged in or out.
 * </p>
 */
public final class PlayerCountMilestones {

    private final Set<Integer> announced = ConcurrentHashMap.newKeySet();

    /**
     * @return true when this threshold has just been reached and should be announced
     */
    public boolean reach(int threshold, int onlineCount) {
        if (onlineCount < threshold) {
            announced.remove(threshold);
            return false;
        }

        return announced.add(threshold);
    }

    public void reset() {
        announced.clear();
    }
}
