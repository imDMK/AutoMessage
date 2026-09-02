package com.github.imdmk.automessage.scheduled.audience.optout;

import com.github.imdmk.automessage.platform.logger.PluginLogger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The players who asked not to receive announcements.
 */
// State rather than configuration, so it is kept out of the okaeri files on purpose: a reload
// must not touch it, nobody edits a list of UUIDs by hand, and it grows with the server rather
// than with the settings. A plain file is also the only store all six platforms already have -
// Bukkit's persistent data container would cover two of them, Minestom saves no player data at
// all, and the other four would need this file anyway.
public final class AnnouncementOptOut {

    private static final String FOLDER = "data";
    private static final String FILE = "muted.txt";

    private final PluginLogger logger;
    private final Path file;

    // Read once per player per announcement, so it is answered from memory.
    private final Set<UUID> muted = ConcurrentHashMap.newKeySet();

    // Nothing has changed until somebody asks for quiet, and a server where nobody ever did
    // should not grow a file to say so.
    private volatile boolean unsaved;

    private AnnouncementOptOut(PluginLogger logger, Path file) {
        this.logger = logger;
        this.file = file;
    }

    public static AnnouncementOptOut load(PluginLogger logger, File dataFolder) {
        final AnnouncementOptOut optOut =
                new AnnouncementOptOut(logger, new File(new File(dataFolder, FOLDER), FILE).toPath());

        optOut.read();

        return optOut;
    }

    public boolean isMuted(UUID player) {
        return muted.contains(player);
    }

    /**
     * Turns announcements off for a player, or back on, and answers with the state they end in.
     */
    public boolean toggle(UUID player) {
        final boolean nowMuted = !muted.remove(player);

        if (nowMuted) {
            muted.add(player);
        }

        this.unsaved = true;

        return nowMuted;
    }

    public int mutedCount() {
        return muted.size();
    }

    private void read() {
        if (!Files.isRegularFile(file)) {
            return;
        }

        int unreadable = 0;

        try {
            for (final String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                final String trimmed = line.trim();

                if (trimmed.isEmpty()) {
                    continue;
                }

                try {
                    muted.add(UUID.fromString(trimmed));
                } catch (IllegalArgumentException exception) {
                    unreadable++;
                }
            }
        } catch (IOException exception) {
            logger.error(exception, "Failed to read %s - no player is muted until it can be read.", file);
            return;
        }

        if (unreadable > 0) {
            // Kept rather than dropped: rewriting the file would throw away lines somebody may
            // have put there, and a bad line only costs one player their preference.
            logger.warn("%d line(s) in %s are not player ids and were skipped.", unreadable, file);
        }

        if (!muted.isEmpty()) {
            logger.info("%d player(s) have announcements turned off.", muted.size());
        }
    }

    /**
     * Writes the list out. Cheap enough to call on every change: it happens at human pace.
     */
    // Written beside the real file and moved over it, so a server that dies mid-write leaves the
    // previous list intact rather than a half of one.
    public synchronized void save() {
        if (!unsaved) {
            return;
        }

        // Cleared before the write rather than after: a change made while this one is in flight
        // marks it again and is picked up by the next save, at the latest on shutdown.
        this.unsaved = false;

        final List<String> lines = new ArrayList<>(muted.size());
        muted.forEach(player -> lines.add(player.toString()));

        try {
            Files.createDirectories(file.getParent());

            final Path pending = file.resolveSibling(FILE + ".tmp");
            Files.write(pending, lines, StandardCharsets.UTF_8);
            Files.move(pending, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            this.unsaved = true;
            logger.error(exception, "Failed to write %s - the change is applied but will not survive a restart.", file);
        }
    }
}
