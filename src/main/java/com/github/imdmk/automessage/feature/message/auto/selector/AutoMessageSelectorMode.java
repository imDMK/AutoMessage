package com.github.imdmk.automessage.feature.message.auto.selector;

/**
 * Represents the strategy used for selecting and dispatching automatic messages.
 * <p>
 * This enum defines how messages should be selected from the configured list.
 */
public enum AutoMessageSelectorMode {

    /**
     * Messages are selected randomly from the configured list.
     * Each dispatch may return a different message, without any defined order.
     */
    RANDOM,

    /**
     * Messages are selected sequentially from the configured list.
     * Each dispatch returns the next message in order, cycling back to the start after the last one.
     */
    SEQUENTIAL

}
