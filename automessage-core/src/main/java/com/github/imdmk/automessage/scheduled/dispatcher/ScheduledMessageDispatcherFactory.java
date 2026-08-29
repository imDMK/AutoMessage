package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.scheduled.selector.MessageSelector;

import java.util.function.Supplier;

/**
 * Builds the dispatcher a single channel uses.
 *
 * <p>
 * Every channel shares the sender and the audience filter but rotates independently, so the
 * selector is the one piece supplied per channel.
 * </p>
 */
@FunctionalInterface
public interface ScheduledMessageDispatcherFactory {

    MessageDispatcher create(Supplier<MessageSelector> selector);
}
