package com.github.imdmk.automessage.scheduled.audience.rule;

import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AudienceRuleTest {

    private static final int TICKS_PER_HOUR = 20 * 60 * 60;

    private static Player playerInWorld(String worldName) {
        Player player = mock(Player.class, RETURNS_DEEP_STUBS);
        World world = mock(World.class);
        when(world.getName()).thenReturn(worldName);
        when(player.getWorld()).thenReturn(world);
        return player;
    }

    private static Player playerWithOnlineCount(int online) {
        Player player = mock(Player.class);
        Server server = mock(Server.class);
        // getOnlinePlayers() is declared as Collection<? extends Player>; the captured wildcard
        // makes thenReturn() unusable here, so the stub is installed the untyped way.
        doReturn(List.copyOf(Collections.nCopies(online, player))).when(server).getOnlinePlayers();
        when(player.getServer()).thenReturn(server);
        return player;
    }

    private static Player playerWithHoursPlayed(int hours) {
        Player player = mock(Player.class);
        when(player.getStatistic(Statistic.PLAY_ONE_MINUTE)).thenReturn(hours * TICKS_PER_HOUR);
        return player;
    }

    private static Player playerWithPermission(String permission) {
        Player player = mock(Player.class);
        when(player.hasPermission(permission)).thenReturn(true);
        return player;
    }

    @Test
    @DisplayName("world rule matches regardless of the case used in the config")
    void worldRuleIsCaseInsensitive() {
        AudienceRule rule = AudienceRule.worlds("World_Nether");

        assertThat(rule.test(playerInWorld("world_nether"))).isTrue();
        assertThat(rule.test(playerInWorld("world"))).isFalse();
    }

    @Test
    @DisplayName("player-count rule bounds the audience at both ends")
    void playerCountRuleRespectsBothBounds() {
        AudienceRule rule = AudienceRule.playerCount(2, 10);

        assertThat(rule.test(playerWithOnlineCount(1))).isFalse();
        assertThat(rule.test(playerWithOnlineCount(2))).isTrue();
        assertThat(rule.test(playerWithOnlineCount(10))).isTrue();
        assertThat(rule.test(playerWithOnlineCount(11))).isFalse();
    }

    @Test
    @DisplayName("an open-ended player-count rule has no upper bound")
    void playerCountRuleCanBeOpenEnded() {
        assertThat(AudiencePlayerCountRule.atLeast(50).test(playerWithOnlineCount(5_000))).isTrue();
    }

    @Test
    @DisplayName("playtime rule separates newcomers from veterans")
    void playTimeRuleBoundsPlaytime() {
        AudienceRule newcomers = AudiencePlayTimeRule.below(Duration.ofHours(2));

        assertThat(newcomers.test(playerWithHoursPlayed(1))).isTrue();
        assertThat(newcomers.test(playerWithHoursPlayed(3))).isFalse();

        AudienceRule veterans = AudiencePlayTimeRule.atLeast(Duration.ofHours(100));

        assertThat(veterans.test(playerWithHoursPlayed(150))).isTrue();
        assertThat(veterans.test(playerWithHoursPlayed(10))).isFalse();
    }

    @Test
    @DisplayName("ANY_OF passes when a single nested rule passes")
    void anyOfIsAnOr() {
        AudienceRule rule = AudienceRule.anyOf(
                AudienceRule.permission("rank.vip"),
                AudienceRule.permission("rank.mod")
        );

        assertThat(rule.test(playerWithPermission("rank.mod"))).isTrue();
        assertThat(rule.test(playerWithPermission("rank.none"))).isFalse();
    }

    @Test
    @DisplayName("NONE_OF excludes everyone matching a nested rule")
    void noneOfIsANor() {
        AudienceRule rule = AudienceRule.noneOf(AudienceRule.permission("rank.vip"));

        assertThat(rule.test(playerWithPermission("rank.vip"))).isFalse();
        assertThat(rule.test(playerWithPermission("something.else"))).isTrue();
    }

    @Test
    @DisplayName("NOT inverts the rule it wraps")
    void notInverts() {
        AudienceRule rule = AudienceRule.not(AudienceRule.permission("hide.ads"));

        assertThat(rule.test(playerWithPermission("hide.ads"))).isFalse();
        assertThat(rule.test(playerWithPermission("other"))).isTrue();
    }

    @Test
    @DisplayName("combinators nest to any depth")
    void combinatorsNest() {
        // VIP or moderator, but never someone who opted out.
        AudienceRule rule = AudienceRule.anyOf(List.of(
                AudienceRule.permission("rank.vip"),
                AudienceRule.anyOf(
                        AudienceRule.permission("rank.mod"),
                        AudienceRule.not(AudienceRule.permission("hide.ads"))
                )
        ));

        assertThat(rule.test(playerWithPermission("rank.vip"))).isTrue();
        assertThat(rule.test(playerWithPermission("rank.mod"))).isTrue();
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
