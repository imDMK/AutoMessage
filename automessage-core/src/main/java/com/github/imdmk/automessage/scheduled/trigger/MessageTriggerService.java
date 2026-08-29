package com.github.imdmk.automessage.scheduled.trigger;

import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.ScheduledMessageRepository;
import com.github.imdmk.automessage.scheduled.audience.filter.AudienceFilter;
import com.github.imdmk.automessage.scheduled.dispatcher.DispatchTarget;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcher;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

/**
 * Decides what a triggering event should send, and to whom.
 *
 * <p>
 * Kept separate from the Bukkit listener so the rules about who receives what can be exercised
 * without a running server.
 * </p>
 */
public final class MessageTriggerService {

    private final Server server;
    private final TaskScheduler taskScheduler;
    private final ScheduledMessageRepository repository;
    private final MessageDispatcher dispatcher;
    private final AudienceFilter filter;
    private final PlayerCountMilestones milestones;

    public MessageTriggerService(
            Server server,
            TaskScheduler taskScheduler,
            ScheduledMessageRepository repository,
            MessageDispatcher dispatcher,
            AudienceFilter filter,
            PlayerCountMilestones milestones
    ) {
        this.server = server;
        this.taskScheduler = taskScheduler;
        this.repository = repository;
        this.dispatcher = dispatcher;
        this.filter = filter;
        this.milestones = milestones;
    }

    /**
     * Sends the join and first-join messages this player qualifies for.
     */
    public void onJoin(Player player) {
        for (final MessageTrigger.Type type : List.of(MessageTrigger.Type.JOIN, MessageTrigger.Type.FIRST_JOIN)) {
            for (final ScheduledMessage message : repository.findByTrigger(type)) {
                if (message.trigger() instanceof JoinTrigger join && join.appliesTo(player)) {
                    sendToJoiner(player, message, join.delay());
                }
            }
        }
    }

    private void sendToJoiner(Player player, ScheduledMessage message, Duration delay) {
        if (delay.isZero()) {
            dispatchTo(player, message);
            return;
        }

        taskScheduler.runLaterSync(() -> {
            // The player can be gone by the time the delay elapses; sending to a disconnected
            // player is at best wasted work.
            if (player.isOnline()) {
                dispatchTo(player, message);
            }
        }, delay);
    }

    private void dispatchTo(Player player, ScheduledMessage message) {
        // Audience rules still apply: a join message restricted to a permission should not reach
        // a player who lacks it just because the trigger fired for them.
        if (filter.allows(player, message)) {
            dispatcher.dispatch(message, DispatchTarget.player(player));
        }
    }

    /**
     * Announces any milestone the current online count has just reached.
     *
     * @param onlineCount the count to test against, passed in because during a join event the
     *                    server's own collection has already been updated and during a quit event
     *                    it has not
     */
    public void onPlayerCountChanged(int onlineCount) {
        final List<ScheduledMessage> messages = repository.findByTrigger(MessageTrigger.Type.PLAYER_COUNT);
        if (messages.isEmpty()) {
            return;
        }

        final Collection<? extends Player> online = server.getOnlinePlayers();
        if (online.isEmpty()) {
            return;
        }

        for (final ScheduledMessage message : messages) {
            if (message.trigger() instanceof PlayerCountTrigger trigger
                    && milestones.reach(trigger.threshold(), onlineCount)) {
                dispatcher.dispatch(message, DispatchTarget.players(online));
            }
        }
    }

    public void reset() {
        milestones.reset();
    }
}
