package com.github.imdmk.automessage.folia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StaleFoliaWarningTest {

    private static final String WARNING =
            "[LiteCommands] Folia detected, but Folia extension is not enabled.";

    private Logger logger;
    private List<String> logged;

    @BeforeEach
    void setUp() {
        this.logged = new ArrayList<>();
        this.logger = Logger.getLogger(StaleFoliaWarningTest.class.getName() + System.nanoTime());
        this.logger.setUseParentHandlers(false);
        this.logger.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                logged.add(record.getMessage());
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        });
    }

    @Test
    @DisplayName("the stale Folia warning does not reach the console")
    void dropsTheStaleWarning() {
        StaleFoliaWarning.silenced(logger, () -> {
            logger.log(Level.WARNING, WARNING);
            return null;
        });

        assertThat(logged).isEmpty();
    }

    @Test
    @DisplayName("everything else logged while it is active still gets through")
    void keepsEverythingElse() {
        StaleFoliaWarning.silenced(logger, () -> {
            logger.log(Level.WARNING, "Something actually went wrong");
            return null;
        });

        assertThat(logged).containsExactly("Something actually went wrong");
    }

    @Test
    @DisplayName("the warning is silenced only for the call, never after it")
    void stopsSilencingAfterwards() {
        StaleFoliaWarning.silenced(logger, () -> null);
        logger.log(Level.WARNING, WARNING);

        assertThat(logged).containsExactly(WARNING);
    }

    @Test
    @DisplayName("a failing call leaves the logger as it found it")
    void restoresOnFailure() {
        final Filter original = record -> true;
        logger.setFilter(original);

        assertThatThrownBy(() -> StaleFoliaWarning.silenced(logger, () -> {
            throw new IllegalStateException("enable failed");
        })).isInstanceOf(IllegalStateException.class);

        assertThat(logger.getFilter()).isSameAs(original);
    }

    @Test
    @DisplayName("a filter that was already there keeps deciding")
    void defersToAnExistingFilter() {
        logger.setFilter(record -> !record.getMessage().contains("hidden"));

        StaleFoliaWarning.silenced(logger, () -> {
            logger.log(Level.WARNING, "hidden by the original filter");
            logger.log(Level.WARNING, "allowed through");
            return null;
        });

        assertThat(logged).containsExactly("allowed through");
    }
}
