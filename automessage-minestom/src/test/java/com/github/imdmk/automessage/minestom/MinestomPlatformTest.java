package com.github.imdmk.automessage.minestom;

import com.github.imdmk.automessage.platform.capability.Capability;
import com.github.imdmk.automessage.platform.scheduler.TaskScheduler;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MinestomPlatformTest {

    private MinestomPlatform platform(boolean realPermissions) {
        return new MinestomPlatform(
                mock(TaskScheduler.class),
                mock(ViewerRegistry.class),
                realPermissions
        );
    }

    @Test
    @DisplayName("should claim no permission capability when falling back to the operator level")
    void shouldNotClaimPermissionsWithoutARealSystem() {
        final var capabilities = platform(false).capabilities();

        assertThat(capabilities.supports(Capability.PERMISSION_RULE)).isFalse();
        assertThat(capabilities.supports(Capability.GROUP_RULE)).isFalse();
    }

    @Test
    @DisplayName("should claim permissions once the server supplies a permission system")
    void shouldClaimPermissionsWithARealSystem() {
        final var capabilities = platform(true).capabilities();

        assertThat(capabilities.supports(Capability.PERMISSION_RULE)).isTrue();
        assertThat(capabilities.supports(Capability.GROUP_RULE)).isTrue();
    }

    @Test
    @DisplayName("should never claim what Minestom keeps no record of")
    void shouldNeverClaimWhatMinestomDoesNotKeep() {
        // True either way: no profiles means no first join, no statistics means no playtime, and
        // an instance is not a world with a name somebody could write in a rule.
        for (final boolean realPermissions : new boolean[] {false, true}) {
            final var capabilities = platform(realPermissions).capabilities();

            assertThat(capabilities.supports(Capability.FIRST_JOIN_TRIGGER)).isFalse();
            assertThat(capabilities.supports(Capability.PLAYTIME_RULE)).isFalse();
            assertThat(capabilities.supports(Capability.WORLD_RULE)).isFalse();
            assertThat(capabilities.supports(Capability.EXTERNAL_PLACEHOLDERS)).isFalse();
        }
    }
}
