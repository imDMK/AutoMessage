package com.github.imdmk.automessage.update;

import com.eternalcode.gitcheck.GitCheckResult;
import com.eternalcode.gitcheck.git.GitException;
import com.eternalcode.gitcheck.git.GitRelease;
import com.github.imdmk.automessage.configuration.implementation.PluginConfiguration;
import com.github.imdmk.automessage.notification.Notification;
import com.github.imdmk.automessage.notification.NotificationSender;
import com.github.imdmk.automessage.notification.implementation.ChatNotification;
import com.github.imdmk.automessage.scheduler.TaskScheduler;
import com.github.imdmk.automessage.text.Formatter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateListener implements Listener {

    private static final String PREFIX = "<dark_gray>[<rainbow>DoubleJump<dark_gray>] ";
    private static final Notification UPDATE_AVAILABLE = new ChatNotification("\n" + PREFIX + "<yellow>A new version is available: {TAG}\n" + PREFIX + "<yellow><u><click:open_url:'{URL}'>Download it here</click></u>\n");
    private static final Notification UPDATE_EXCEPTION = new ChatNotification(PREFIX + "<red>An error occurred while checking for update: {MESSAGE}");

    private final PluginConfiguration pluginConfiguration;
    private final NotificationSender notificationSender;
    private final UpdateService updateService;
    private final TaskScheduler taskScheduler;

    public UpdateListener(PluginConfiguration pluginConfiguration, NotificationSender notificationSender, UpdateService updateService, TaskScheduler taskScheduler) {
        this.pluginConfiguration = pluginConfiguration;
        this.notificationSender = notificationSender;
        this.updateService = updateService;
        this.taskScheduler = taskScheduler;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!this.pluginConfiguration.checkForUpdate) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.isOp()) {
            return;
        }

        this.taskScheduler.runAsync(() -> this.checkForUpdate(player));
    }

    private void checkForUpdate(Player player) {
        try {
            GitCheckResult gitCheckResult = this.updateService.check();
            if (gitCheckResult.isUpToDate()) {
                return;
            }

            GitRelease latestRelease = gitCheckResult.getLatestRelease();

            Formatter formatter = new Formatter()
                    .placeholder("{TAG}", latestRelease.getTag())
                    .placeholder("{URL}", latestRelease.getPageUrl());

            this.notificationSender.sendNotification(player, UPDATE_AVAILABLE, formatter);
        }
        catch (GitException gitException) {
            Formatter formatter = new Formatter()
                    .placeholder("{MESSAGE}", gitException.getMessage());

            this.notificationSender.sendNotification(player, UPDATE_EXCEPTION, formatter);
        }
    }
}
