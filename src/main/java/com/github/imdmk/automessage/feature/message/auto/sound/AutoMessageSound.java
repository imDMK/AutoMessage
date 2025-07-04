package com.github.imdmk.automessage.feature.message.auto.sound;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a sound effect configuration to be played during a jump.
 * <p>
 * Wraps a {@link Sound} along with volume and pitch parameters
 *
 * @param sound  the {@link Sound} to be played (must not be null)
 * @param volume the volume of the sound (typically between 0.0 and 1.0+)
 * @param pitch  the pitch of the sound (typically between 0.5 and 2.0)
 */
public record AutoMessageSound(@NotNull Sound sound, float volume, float pitch) {

    /**
     * Plays the configured sound effect for the specified player at their current location.
     *
     * @param player the {@link Player} to play the sound for (must not be null)
     */
    public void play(@NotNull Player player) {
        player.playSound(player.getLocation(), this.sound, this.volume, this.pitch);
    }
}
