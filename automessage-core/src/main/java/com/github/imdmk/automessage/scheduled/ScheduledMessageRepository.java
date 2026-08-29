package com.github.imdmk.automessage.scheduled;

import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
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
     * Finds a message by its configured name, ignoring case.
     *
     * @param name name to look for, may be null
     * @return the matching message, or empty when no message uses that name
     */
    Optional<ScheduledMessage> findByName(String name);

    /**
     * @param channel channel to read
     * @return the messages that joined the given channel, in configuration order
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
