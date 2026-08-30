package com.github.imdmk.automessage.scheduled.selector;

public enum MessageSelectorType {

    /** Independent draw each time; the same message can come up twice in a row. */
    RANDOM,

    /** Random order, but every message is shown once before any repeats. */
    SHUFFLE,

    /** Fixed order, cycling from the top. */
    SEQUENTIAL,

    /** Random draw biased by each message's {@code weight}. */
    WEIGHTED,
}
