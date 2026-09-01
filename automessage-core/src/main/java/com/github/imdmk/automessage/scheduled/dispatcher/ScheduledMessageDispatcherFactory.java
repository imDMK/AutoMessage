package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.scheduled.selector.MessageSelector;

import java.util.function.Supplier;

@FunctionalInterface
public interface ScheduledMessageDispatcherFactory {

    MessageDispatcher create(Supplier<MessageSelector> selector);
}
