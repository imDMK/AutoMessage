package com.github.imdmk.automessage.bukkit.listener;

import com.github.imdmk.automessage.scheduled.trigger.MessageTriggerService;

import com.github.imdmk.automessage.bukkit.BukkitViewerFactory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class MessageTriggerListener implements Listener {

    private final BukkitViewerFactory viewers;
    private final MessageTriggerService triggerService;

    public MessageTriggerListener(BukkitViewerFactory viewers, MessageTriggerService triggerService) {
        this.viewers = viewers;
        this.triggerService = triggerService;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        triggerService.onJoin(viewers.of(event.getPlayer()), !event.getPlayer().hasPlayedBefore());

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
