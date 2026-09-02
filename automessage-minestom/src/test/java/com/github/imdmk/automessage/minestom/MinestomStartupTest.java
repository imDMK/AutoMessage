package com.github.imdmk.automessage.minestom;

import net.minestom.server.MinecraftServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

// Minestom is a library, so the only platform here whose real runtime can be started inside a
// test - no server jar, no agreement with anybody, no port to bind.
class MinestomStartupTest {

    @TempDir
    Path dataDirectory;

    @Test
    @DisplayName("should start and stop against a real Minestom runtime")
    void shouldStartAgainstARealRuntime() {
        MinecraftServer.init();

        final AutoMessageMinestom automessage = AutoMessageMinestom.builder()
                .dataDirectory(dataDirectory)
                .enable();

        try {
            // The files a server administrator would find, written by the real okaeri against a
            // real Minestom scheduler and connection manager.
            assertThat(new File(dataDirectory.toFile(), "config.yml")).exists();
            assertThat(new File(dataDirectory.toFile(), "scheduledMessages.yml")).exists();
            assertThat(new File(dataDirectory.toFile(), "lang/en.yml")).exists();

            // Falling back to the operator level, so the permission-gated example is not offered.
            assertThat(new File(dataDirectory.toFile(), "scheduledMessages.yml"))
                    .content()
                    .doesNotContain("vip-perk-reminder")
                    .contains("vote-reminder");
        } finally {
            assertThatCode(automessage::shutdown).doesNotThrowAnyException();
        }
    }
}
