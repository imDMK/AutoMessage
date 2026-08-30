package com.github.imdmk.automessage.scheduled;

import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import com.github.imdmk.automessage.scheduled.trigger.MessageTrigger;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;

/**
 * Read access to the scheduled messages currently configured in {@code scheduledMessages.yml}.
 *
 * <p>
 * Implementations must reflect the configuration as it is at the moment of the call, so callers
 * keep working after {@code /automessage reload} without being re-created.
 * </p>
 */
public interface ScheduledMessageRepository {

    /**
     * @return every configured message, in configuration order
     */
    @Unmodifiable
    List<ScheduledMessage> findAll();

    /**
     * @return the messages that take part in the timed rotation, in configuration order
     */
    @Unmodifiable
    List<ScheduledMessage> findScheduled();

    /**
     * @param type trigger type to look for
     * @return the messages fired by that kind of event, in configuration order
     */
    @Unmodifiable
    List<ScheduledMessage> findByTrigger(MessageTrigger.Type type);

    /**
     * Finds a message by its configured name, ignoring case.
     *
     * @param name name to look for, may be null
     * @return the matching message, or empty when no message uses that name
     */
    Optional<ScheduledMessage> findByName(String name);

    /**
     * @param channel channel to read
     * @return the rotating messages that joined the given channel, in configuration order.
     *         Triggered messages are excluded: they fire on their event, and a channel picking
     *         one up would also announce it at an arbitrary moment.
     */
    @Unmodifiable
    List<ScheduledMessage> findByChannel(AnnouncementChannel channel);

    /**
     * @return the names of every configured message, in configuration order
     */
    @Unmodifiable
    List<String> names();

    /**
     * Creates a repository backed by the given configuration file.
     *
     * @param config configuration holding the scheduled messages
     * @return repository reading straight from the configuration
     */
    static ScheduledMessageRepository config(ScheduledMessagesConfig config) {
        return new ConfigScheduledMessageRepository(config);
    }
}
