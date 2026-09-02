package com.github.imdmk.automessage.scheduled;

import com.github.imdmk.automessage.config.ConfigReloadService;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import com.github.imdmk.automessage.scheduled.trigger.MessageTrigger;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;

public interface ScheduledMessageRepository {

    @Unmodifiable
    List<ScheduledMessage> findAll();

    @Unmodifiable
    List<ScheduledMessage> findScheduled();

    @Unmodifiable
    List<ScheduledMessage> findByTrigger(MessageTrigger.Type type);

    Optional<ScheduledMessage> findByName(String name);

    @Unmodifiable
    List<ScheduledMessage> findByChannel(AnnouncementChannel channel);

    @Unmodifiable
    List<String> names();

    static ScheduledMessageRepository config(
            ScheduledMessagesConfig config,
            ConfigReloadService reloadService
    ) {
        final ConfigScheduledMessageRepository repository = new ConfigScheduledMessageRepository(config);
        reloadService.register(repository);

        return repository;
    }
}
