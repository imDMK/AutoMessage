package com.github.imdmk.automessage.support;

import com.github.imdmk.automessage.platform.viewer.Viewer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

// A player that remembers everything sent to it, so a test can assert on what a reader would
// actually have seen rather than on the calls that produced it.
public final class RecordingViewer implements Viewer, Audience {

    private final String name;
    private final String locale;
    private final @Nullable String world;
    private final Set<String> permissions;

    public final List<String> chat = new ArrayList<>();
    public final List<String> actionBars = new ArrayList<>();
    public final List<Title> titles = new ArrayList<>();
    public final List<BossBar> bossBars = new ArrayList<>();
    public final List<Sound> sounds = new ArrayList<>();

    private boolean online = true;

    public RecordingViewer(String name, String locale, @Nullable String world, String... permissions) {
        this.name = name;
        this.locale = locale;
        this.world = world;
        this.permissions = Set.of(permissions);
    }

    public static RecordingViewer english(String name, String... permissions) {
        return new RecordingViewer(name, "en_us", "world", permissions);
    }

    public List<String> everythingSeen() {
        final List<String> seen = new ArrayList<>(chat);
        seen.addAll(actionBars);
        titles.forEach(title -> seen.add(plain(title.title())));
        bossBars.forEach(bar -> seen.add(plain(bar.name())));

        return seen;
    }

    public void forget() {
        chat.clear();
        actionBars.clear();
        titles.clear();
        bossBars.clear();
        sounds.clear();
    }

    public void disconnect() {
        this.online = false;
    }

    private static String plain(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    @Override
    public String name() {
        return name;
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
        return locale;
    }

    @Override
    public Optional<String> world() {
        return Optional.ofNullable(world);
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
        return online;
    }

    @Override
    public Audience audience() {
        return this;
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
    public void showBossBar(BossBar bar) {
        bossBars.add(bar);
    }

    @Override
    public void hideBossBar(BossBar bar) {
        bossBars.remove(bar);
    }

    @Override
    public void playSound(Sound sound) {
        sounds.add(sound);
    }
}
