package com.github.imdmk.automessage.config;

import com.github.imdmk.automessage.platform.capability.Capabilities;
import com.github.imdmk.automessage.platform.capability.Capability;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommentDirectivesTest {

    private static final Capabilities WITH_WORLDS = Capabilities.of(Capability.WORLD_RULE);
    private static final Capabilities WITHOUT_WORLDS = Capabilities.of(Capability.PLAYTIME_RULE);

    private static final String[] BLOCK = {
            "# who receives it",
            "@requires WORLD_RULE",
            "#   - type: WORLD",
            "#     worlds: [world]",
            "@end",
            "#   - type: PLAYER_COUNT"
    };

    @Test
    @DisplayName("should keep a documented block the platform can honour")
    void shouldKeepASupportedBlock() {
        assertThat(CommentDirectives.apply(BLOCK, WITH_WORLDS)).containsExactly(
                "# who receives it",
                "#   - type: WORLD",
                "#     worlds: [world]",
                "#   - type: PLAYER_COUNT"
        );
    }

    @Test
    @DisplayName("should drop a documented block the platform cannot honour")
    void shouldDropAnUnsupportedBlock() {
        assertThat(CommentDirectives.apply(BLOCK, WITHOUT_WORLDS)).containsExactly(
                "# who receives it",
                "#   - type: PLAYER_COUNT"
        );
    }

    @Test
    @DisplayName("should leave a comment with no directives exactly as it was")
    void shouldLeaveAPlainCommentAlone() {
        final String[] plain = {"# nothing platform-specific here"};

        assertThat(CommentDirectives.apply(plain, WITHOUT_WORLDS)).isSameAs(plain);
    }

    @Test
    @DisplayName("should refuse a capability name that does not exist")
    void shouldRefuseAnUnknownCapability() {
        // A typo would quietly keep documentation nobody can act on - the exact failure this
        // whole mechanism exists to prevent.
        final String[] lines = {"@requires WROLD_RULE", "# text", "@end"};

        assertThatThrownBy(() -> CommentDirectives.apply(lines, WITH_WORLDS))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("WROLD_RULE");
    }

    @Test
    @DisplayName("should refuse a block that was never closed")
    void shouldRefuseAnUnclosedBlock() {
        final String[] lines = {"@requires WORLD_RULE", "# text"};

        assertThatThrownBy(() -> CommentDirectives.apply(lines, WITH_WORLDS))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("@end");
    }

    @Test
    @DisplayName("should refuse an end with nothing open")
    void shouldRefuseAStrayEnd() {
        final String[] lines = {"# text", "@end"};

        assertThatThrownBy(() -> CommentDirectives.apply(lines, WITH_WORLDS))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("@requires");
    }
}
