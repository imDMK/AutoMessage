package com.github.imdmk.automessage.scheduled.audience.rule;

import com.github.imdmk.automessage.platform.viewer.PlaytimeSource;
import com.github.imdmk.automessage.platform.viewer.Viewer;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;
import net.kyori.adventure.audience.Audience;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AudienceRuleTest {

    private record FakeViewer(String name, String worldName, Set<String> permissions) implements Viewer {

        static FakeViewer inWorld(String world) {
            return new FakeViewer("Steve", world, Set.of());
        }

        static FakeViewer with(String... permissions) {
            return new FakeViewer("Steve", "world", Set.of(permissions));
        }

        static FakeViewer plain() {
            return new FakeViewer("Steve", null, Set.of());
        }

        @Override
        public UUID uniqueId() {
            return UUID.nameUUIDFromBytes(name.getBytes());
        }

        @Override
        public String displayName() {
            return name;
        }

        @Override
        public String locale() {
            return "en_us";
        }

        @Override
        public Optional<String> world() {
            return Optional.ofNullable(worldName);
        }

        @Override
        public boolean hasPermission(String permission) {
            return permissions.contains(permission);
        }

        @Override
        public boolean isPlayer() {
            return true;
        }

        @Override
        public boolean isOnline() {
            return true;
        }

        @Override
        public Audience audience() {
            return Audience.empty();
        }
    }

    private static AudienceContext context(int online, Duration playtime) {
        ViewerRegistry viewers = new ViewerRegistry() {

            @Override
            public Collection<Viewer> online() {
                return List.of();
            }

            @Override
            public int onlineCount() {
                return online;
            }

            @Override
            public int maxPlayers() {
                return 100;
            }
        };

        PlaytimeSource playtimeSource = playtime == null
                ? PlaytimeSource.unavailable()
                : viewer -> Optional.of(playtime);

        return AudienceContext.of(viewers, playtimeSource);
    }

    private static final AudienceContext ANY = context(1, Duration.ofHours(1));

    @Test
    @DisplayName("permission and group rules read the viewer's permissions")
    void permissionRules() {
        assertThat(AudienceRule.permission("rank.vip").test(FakeViewer.with("rank.vip"), ANY)).isTrue();
        assertThat(AudienceRule.permission("rank.vip").test(FakeViewer.with("other"), ANY)).isFalse();

        // A group rule is a permission rule with a prefix, which is how permission plugins grant them.
        assertThat(AudienceRule.group("vip").test(FakeViewer.with("group.vip"), ANY)).isTrue();
    }

    @Test
    @DisplayName("world rule matches regardless of the case used in the config")
    void worldRuleIsCaseInsensitive() {
        AudienceRule rule = AudienceRule.worlds("World_Nether");

        assertThat(rule.test(FakeViewer.inWorld("world_nether"), ANY)).isTrue();
        assertThat(rule.test(FakeViewer.inWorld("world"), ANY)).isFalse();
    }

    @Test
    @DisplayName("a viewer with no world matches no world rule instead of throwing")
    void worldRuleWithoutAWorld() {
        // The console, and everyone on a proxy.
        assertThat(AudienceRule.worlds("world").test(FakeViewer.plain(), ANY)).isFalse();
    }

    @Test
    @DisplayName("player-count rule bounds the audience at both ends")
    void playerCountRuleRespectsBothBounds() {
        AudienceRule rule = AudienceRule.playerCount(2, 10);

        assertThat(rule.test(FakeViewer.plain(), context(1, null))).isFalse();
        assertThat(rule.test(FakeViewer.plain(), context(2, null))).isTrue();
        assertThat(rule.test(FakeViewer.plain(), context(10, null))).isTrue();
        assertThat(rule.test(FakeViewer.plain(), context(11, null))).isFalse();
    }

    @Test
    @DisplayName("an open-ended player-count rule has no upper bound")
    void playerCountRuleCanBeOpenEnded() {
        assertThat(new AudiencePlayerCountRule(50, AudiencePlayerCountRule.UNBOUNDED).test(FakeViewer.plain(), context(5_000, null))).isTrue();
    }

    @Test
    @DisplayName("playtime rule separates newcomers from veterans")
    void playTimeRuleBoundsPlaytime() {
        AudienceRule newcomers = new AudiencePlayTimeRule(Duration.ZERO, Duration.ofHours(2));

        assertThat(newcomers.test(FakeViewer.plain(), context(1, Duration.ofHours(1)))).isTrue();
        assertThat(newcomers.test(FakeViewer.plain(), context(1, Duration.ofHours(3)))).isFalse();
    }

    @Test
    @DisplayName("a platform that keeps no playtime matches nobody rather than everybody")
    void playTimeRuleWithoutASource() {
        // Treating "unknown" as zero would make every veteran on a proxy look like a newcomer.
        assertThat(new AudiencePlayTimeRule(Duration.ZERO, Duration.ofHours(2))
                .test(FakeViewer.plain(), context(1, null))).isFalse();
    }

    @Test
    @DisplayName("ANY_OF passes when a single nested rule passes")
    void anyOfIsAnOr() {
        AudienceRule rule = AudienceRule.anyOf(
                AudienceRule.permission("rank.vip"),
                AudienceRule.permission("rank.mod")
        );

        assertThat(rule.test(FakeViewer.with("rank.mod"), ANY)).isTrue();
        assertThat(rule.test(FakeViewer.with("rank.none"), ANY)).isFalse();
    }

    @Test
    @DisplayName("NONE_OF excludes everyone matching a nested rule")
    void noneOfIsANor() {
        AudienceRule rule = AudienceRule.noneOf(AudienceRule.permission("rank.vip"));

        assertThat(rule.test(FakeViewer.with("rank.vip"), ANY)).isFalse();
        assertThat(rule.test(FakeViewer.with("something.else"), ANY)).isTrue();
    }

    @Test
    @DisplayName("NOT inverts the rule it wraps")
    void notInverts() {
        AudienceRule rule = AudienceRule.not(AudienceRule.permission("hide.ads"));

        assertThat(rule.test(FakeViewer.with("hide.ads"), ANY)).isFalse();
        assertThat(rule.test(FakeViewer.with("other"), ANY)).isTrue();
    }

    @Test
    @DisplayName("combinators nest to any depth")
    void combinatorsNest() {
        AudienceRule rule = AudienceRule.anyOf(List.of(
                AudienceRule.permission("rank.vip"),
                AudienceRule.anyOf(
                        AudienceRule.permission("rank.mod"),
                        AudienceRule.not(AudienceRule.permission("hide.ads"))
                )
        ));

        assertThat(rule.test(FakeViewer.with("rank.vip"), ANY)).isTrue();
        assertThat(rule.test(FakeViewer.with("rank.mod"), ANY)).isTrue();
    }

    @Test
    @DisplayName("rejects a range whose maximum sits below its minimum")
    void rejectsInvertedRanges() {
        assertThatThrownBy(() -> AudienceRule.playerCount(10, 2))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> AudienceRule.playTime(Duration.ofHours(5), Duration.ofHours(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("a combinator without nested rules is a configuration error")
    void emptyCombinatorsAreRejected() {
        assertThatThrownBy(() -> AudienceRule.anyOf(List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
