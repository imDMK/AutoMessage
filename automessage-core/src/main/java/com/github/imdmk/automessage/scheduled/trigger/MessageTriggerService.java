package com.github.imdmk.automessage.scheduled.trigger;

import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.ScheduledMessageRepository;
import com.github.imdmk.automessage.scheduled.audience.filter.AudienceFilter;
import com.github.imdmk.automessage.scheduled.dispatcher.DispatchTarget;
import com.github.imdmk.automessage.scheduled.dispatcher.MessageDispatcher;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;
import com.github.imdmk.automessage.scheduled.audience.rule.AudienceContext;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

public final class MessageTriggerService {

    private final ViewerRegistry viewers;
    private final TaskScheduler taskScheduler;
    private final ScheduledMessageRepository repository;
    private final MessageDispatcher dispatcher;
    private final AudienceFilter filter;
    private final AudienceContext audienceContext;
    private final PlayerCountMilestones milestones;

    public MessageTriggerService(
            ViewerRegistry viewers,
            TaskScheduler taskScheduler,
            ScheduledMessageRepository repository,
            MessageDispatcher dispatcher,
            AudienceFilter filter,
            AudienceContext audienceContext,
            PlayerCountMilestones milestones
    ) {
        this.viewers = viewers;
        this.taskScheduler = taskScheduler;
        this.repository = repository;
        this.dispatcher = dispatcher;
        this.filter = filter;
        this.audienceContext = audienceContext;
        this.milestones = milestones;
    }

    public void onJoin(Viewer viewer, boolean firstJoin) {
        for (final MessageTrigger.Type type : List.of(MessageTrigger.Type.JOIN, MessageTrigger.Type.FIRST_JOIN)) {
            for (final ScheduledMessage message : repository.findByTrigger(type)) {
                if (message.trigger() instanceof JoinTrigger join && join.appliesTo(firstJoin)) {
                    sendToJoiner(viewer, message, join.delay());
                }
            }
        }
    }

    private void sendToJoiner(Viewer viewer, ScheduledMessage message, Duration delay) {
        if (delay.isZero()) {
            dispatchTo(viewer, message);
            return;
        }

        taskScheduler.runLaterSync(() -> {
            // The player can be gone by the time the delay elapses; sending to a disconnected
            // player is at best wasted work.
            if (viewer.isOnline()) {
                dispatchTo(viewer, message);
            }
        }, delay);
    }

    private void dispatchTo(Viewer viewer, ScheduledMessage message) {
        // Not the rules themselves - the dispatcher applies those to whoever it is handed. This
        // asks first so a trigger that reaches nobody is not dispatched at all: the dispatcher
        // tells its observers about every announcement it is given, recipients or none, and a
        // greeting for a player who fails the rules would otherwise show up in the statistics.
        if (filter.allows(viewer, message, audienceContext)) {
            dispatcher.dispatch(message, DispatchTarget.viewer(viewer));
        }
    }

    public void onPlayerCountChanged(int onlineCount) {
        final List<ScheduledMessage> messages = repository.findByTrigger(MessageTrigger.Type.PLAYER_COUNT);
        if (messages.isEmpty()) {
            return;
        }

        final Collection<Viewer> online = viewers.online();
        if (online.isEmpty()) {
            return;
        }

        for (final ScheduledMessage message : messages) {
            if (message.trigger() instanceof PlayerCountTrigger trigger
                    && milestones.reach(trigger.threshold(), onlineCount)) {
                dispatcher.dispatch(message, DispatchTarget.viewers(online));
            }
        }
    }
}
