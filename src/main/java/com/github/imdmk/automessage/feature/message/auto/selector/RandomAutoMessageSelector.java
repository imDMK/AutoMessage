package com.github.imdmk.automessage.feature.message.auto.selector;

import com.github.imdmk.automessage.feature.message.auto.AutoMessageNotice;
import com.github.imdmk.automessage.feature.message.auto.eligibility.AutoMessageEligibilityEvaluator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

final class RandomAutoMessageSelector implements AutoMessageSelector {

    private final AutoMessageEligibilityEvaluator evaluator;

    private final Random random = new Random();

    public RandomAutoMessageSelector(@NotNull AutoMessageEligibilityEvaluator evaluator) {
        this.evaluator = Objects.requireNonNull(evaluator, "evaluator cannot be null");
    }

    @Override
    public Optional<AutoMessageNotice> selectFor(@NotNull Player player, @NotNull List<AutoMessageNotice> messages) {
        List<AutoMessageNotice> filtered = messages.stream()
                .filter(notice -> this.evaluator.canReceive(player, notice))
                .toList();

        if (filtered.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(filtered.get(this.random.nextInt(filtered.size())));
    }

    @Override
    public void reset() {}
}
