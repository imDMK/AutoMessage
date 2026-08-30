package com.github.imdmk.automessage.scheduled.trigger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Turns Bukkit events into trigger evaluations.
 *
 * <p>
 * Both handlers run at {@link EventPriority#MONITOR} and change nothing about the event: this
 * listener only observes.
 * </p>
 */
public final class MessageTriggerListener implements Listener {

    private final MessageTriggerService triggerService;

    public MessageTriggerListener(MessageTriggerService triggerService) {
        this.triggerService = triggerService;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        triggerService.onJoin(event.getPlayer());

        // The joining player is already part of the server's collection by the time this fires.
        triggerService.onPlayerCountChanged(event.getPlayer().getServer().getOnlinePlayers().size());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        // The leaving player is still counted here, so the post-quit population is one lower.
        // Milestones need that number to rearm at the right moment.
        final int remaining = event.getPlayer().getServer().getOnlinePlayers().size() - 1;

        triggerService.onPlayerCountChanged(Math.max(0, remaining));
    }
}
