package com.github.imdmk.automessage.notice;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NoticeRendererTest {

    private static final class RecordingAudience implements Audience {

        private final List<String> chat = new ArrayList<>();
        private final List<String> actionBars = new ArrayList<>();
        private final List<Title> titles = new ArrayList<>();
        private final List<BossBar> shownBars = new ArrayList<>();
        private final List<BossBar> hiddenBars = new ArrayList<>();
        private final List<Sound> sounds = new ArrayList<>();
        private int titlesCleared;

        private static String plain(Component component) {
            return PlainTextComponentSerializer.plainText().serialize(component);
        }

        @Override
        public void sendMessage(Component message) {
            chat.add(plain(message));
        }

        @Override
        public void sendActionBar(Component message) {
            actionBars.add(plain(message));
        }

        @Override
        public void showTitle(Title title) {
            titles.add(title);
        }

        @Override
        public void clearTitle() {
            titlesCleared++;
        }

        @Override
        public void showBossBar(BossBar bar) {
            shownBars.add(bar);
        }

        @Override
        public void hideBossBar(BossBar bar) {
            hiddenBars.add(bar);
        }

        @Override
        public void playSound(Sound sound) {
            sounds.add(sound);
        }
    }

    private final RecordingAudience audience = new RecordingAudience();
    private final NoticeRenderer renderer = NoticeRenderer.miniMessage(NoticeDelayer.immediate());

    @Test
    @DisplayName("sends every chat line, with the formatting parsed rather than shown")
    void rendersChat() {
        renderer.render(Notice.chat("<red>first", "<gray>second"), audience);

        assertThat(audience.chat).containsExactly("first", "second");
    }

    @Test
    @DisplayName("sends an action bar")
    void rendersActionBar() {
        renderer.render(Notice.actionBar("<yellow>above the hotbar"), audience);

        assertThat(audience.actionBars).containsExactly("above the hotbar");
    }

    @Test
    @DisplayName("a title and its subtitle arrive as one title, not two")
    void rendersTitleAsOne() {
        renderer.render(Notice.title("<red>Title", "<gray>Sub"), audience);

        assertThat(audience.titles).hasSize(1);
        assertThat(RecordingAudience.plain(audience.titles.getFirst().title())).isEqualTo("Title");
        assertThat(RecordingAudience.plain(audience.titles.getFirst().subtitle())).isEqualTo("Sub");
    }

    @Test
    @DisplayName("a title written alone still arrives, with an empty subtitle")
    void rendersTitleWithoutSubtitle() {
        renderer.render(Notice.title("<red>Only"), audience);

        assertThat(audience.titles).hasSize(1);
        assertThat(RecordingAudience.plain(audience.titles.getFirst().subtitle())).isEmpty();
    }

    @Test
    @DisplayName("title times reach the title rather than being dropped")
    void rendersTitleTimes() {
        renderer.render(
                Notice.title("<red>T", "<gray>S", Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500)),
                audience
        );

        Title.Times times = audience.titles.getFirst().times();

        assertThat(times).isNotNull();
        assertThat(times.stay()).isEqualTo(Duration.ofSeconds(3));
    }

    @Test
    @DisplayName("hideTitle clears whatever is on screen")
    void clearsTitle() {
        renderer.render(Notice.hideTitle(), audience);

        assertThat(audience.titlesCleared).isEqualTo(1);
    }

    @Test
    @DisplayName("a boss bar goes up and is taken away again")
    void showsAndHidesBossBar() {
        List<Duration> delays = new ArrayList<>();
        NoticeRenderer timed = NoticeRenderer.miniMessage((delay, action) -> {
            delays.add(delay);
            action.run();
        });

        timed.render(
                Notice.bossBar(BossBar.Color.RED, BossBar.Overlay.PROGRESS, Duration.ofSeconds(5), 0.5D, "<green>bar"),
                audience
        );

        assertThat(audience.shownBars).hasSize(1);
        assertThat(audience.hiddenBars).containsExactlyElementsOf(audience.shownBars);
        assertThat(delays).containsExactly(Duration.ofSeconds(5));
        assertThat(audience.shownBars.getFirst().progress()).isEqualTo(0.5F);
    }

    @Test
    @DisplayName("a boss bar with no progress configured is full")
    void bossBarDefaultsToFull() {
        renderer.render(
                Notice.bossBar(BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_10, Duration.ofSeconds(3), "<green>bar"),
                audience
        );

        assertThat(audience.shownBars.getFirst().progress()).isEqualTo(BossBar.MAX_PROGRESS);
    }

    @Test
    @DisplayName("plays a sound with the volume and pitch it was given")
    void playsSound() {
        renderer.render(
                Notice.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 2.0F, 7.0F),
                audience
        );

        Sound sound = audience.sounds.getFirst();

        assertThat(sound.name()).isEqualTo(Key.key("block.note_block.pling"));
        assertThat(sound.volume()).isEqualTo(2.0F);
        assertThat(sound.pitch()).isEqualTo(7.0F);
    }

    @Test
    @DisplayName("placeholders are substituted before the text is parsed")
    void substitutesBeforeParsing() {
        renderer.render(Notice.chat("<gray>online: {ONLINE}"), audience,
                text -> text.replace("{ONLINE}", "47"));

        assertThat(audience.chat).containsExactly("online: 47");
    }

    @Test
    @DisplayName("delivers every part of a multi-part notice")
    void rendersEveryPart() {
        renderer.render(
                Notice.of(
                        ChatPart.of("<gray>chat"),
                        new ActionBarPart("<yellow>bar"),
                        SoundPart.of(Key.key("entity.experience_orb.pickup"))
                ),
                audience
        );

        assertThat(audience.chat).hasSize(1);
        assertThat(audience.actionBars).hasSize(1);
        assertThat(audience.sounds).hasSize(1);
    }
}
