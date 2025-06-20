package com.github.imdmk.automessage.feature.message.auto.selector;

import com.github.imdmk.automessage.feature.message.auto.AutoMessageNotice;
import com.github.imdmk.automessage.feature.message.auto.eligibility.AutoMessageEligibilityEvaluator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

final class SequentialAutoMessageSelector implements AutoMessageSelector {

    private static final int INITIAL_POSITION = 0;

    private final AutoMessageEligibilityEvaluator evaluator;
    private final AtomicInteger position = new AtomicInteger(INITIAL_POSITION);

    public SequentialAutoMessageSelector(@NotNull AutoMessageEligibilityEvaluator evaluator) {
        this.evaluator = Objects.requireNonNull(evaluator, "evaluator cannot be null");
    }

    @Override
    public Optional<AutoMessageNotice> selectFor(@NotNull Player player, @NotNull List<AutoMessageNotice> messages) {
        int size = messages.size();

        for (int i = 0; i < size; i++) {
            int index = Math.floorMod(this.position.getAndIncrement(), size);;

            AutoMessageNotice candidate = messages.get(index);
            if (candidate == null) {
                return Optional.empty();
            }

            if (this.evaluator.canReceive(player, candidate)) {
                return Optional.of(candidate);
            }
        }

        return Optional.empty();
    }

    @Override
    public void reset() {
        this.position.set(INITIAL_POSITION);
    }
}
