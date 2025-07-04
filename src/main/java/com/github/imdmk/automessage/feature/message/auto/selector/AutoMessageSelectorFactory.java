package com.github.imdmk.automessage.feature.message.auto.selector;

import com.github.imdmk.automessage.feature.message.auto.eligibility.AutoMessageEligibilityEvaluator;
import org.jetbrains.annotations.NotNull;

/**
 * Factory for creating {@link AutoMessageSelector} instances based on the specified mode.
 */
public final class AutoMessageSelectorFactory {

    private AutoMessageSelectorFactory() {
        throw new UnsupportedOperationException("Factory class cannot be instantiated.");
    }

    /**
     * Creates an {@link AutoMessageSelector} according to the given {@link AutoMessageSelectorMode}.
     *
     * @param mode      the selection mode, must not be {@code null}
     * @param evaluator the eligibility evaluator used by the selector, must not be {@code null}
     * @return a new instance of {@link AutoMessageSelector} matching the specified mode
     */
    public static AutoMessageSelector create(
            @NotNull AutoMessageSelectorMode mode,
            @NotNull AutoMessageEligibilityEvaluator evaluator
    ) {
        return switch (mode) {
            case SEQUENTIAL -> new SequentialAutoMessageSelector(evaluator);
            case RANDOM -> new RandomAutoMessageSelector(evaluator);
        };
    }
}
