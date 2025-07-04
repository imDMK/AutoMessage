package com.github.imdmk.automessage.feature.message.auto;

import com.eternalcode.multification.notice.Notice;
import com.github.imdmk.automessage.feature.message.auto.sound.AutoMessageSound;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents an auto-message notice with optional permission and group requirements.
 */
public class AutoMessageNotice {

    private final @NotNull List<Notice> notices;

    private final @Nullable AutoMessageSound sound;
    private final @Nullable String requiredPermission;
    private final @Nullable String requiredGroup;

    private AutoMessageNotice(
            @NotNull List<Notice> notices,
            @Nullable AutoMessageSound sound,
            @Nullable String requiredPermission,
            @Nullable String requiredGroup
    ) {
        this.notices = Objects.requireNonNull(notices, "notice cannot be null");
        this.sound = sound;
        this.requiredPermission = requiredPermission;
        this.requiredGroup = requiredGroup;
    }

    /**
     * @return the associated {@link Notice}
     */
    public @Unmodifiable List<Notice> getNotices() {
        return Collections.unmodifiableList(this.notices);
    }

    /**
     * @return the associated {@link AutoMessageSound}
     */
    public Optional<AutoMessageSound> getSound() {
        return Optional.ofNullable(this.sound);
    }

    /**
     * @return optional required permission
     */
    public Optional<String> getRequiredPermission() {
        return Optional.ofNullable(this.requiredPermission);
    }

    /**
     * @return optional required group
     */
    public Optional<String> getRequiredGroup() {
        return Optional.ofNullable(this.requiredGroup);
    }

    /**
     * Creates a new builder instance.
     *
     * @return the builder
     */
    public static @NotNull Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link AutoMessageNotice}.
     */
    public static class Builder {

        private @NotNull List<Notice> notices = new ArrayList<>();

        private @Nullable AutoMessageSound sound;
        private @Nullable String requiredPermission;
        private @Nullable String requiredGroup;

        @Contract("_ -> this")
        @CheckReturnValue
        public @NotNull Builder notice(@NotNull Notice notice) {
            Objects.requireNonNull(notice, "notice cannot be null");
            this.notices.add(notice);
            return this;
        }

        @Contract("_ -> this")
        @CheckReturnValue
        public @NotNull Builder notices(@NotNull List<Notice> notices) {
            Objects.requireNonNull(notices, "notices cannot be null");
            this.notices = notices;
            return this;
        }

        @Contract("_ -> this")
        @CheckReturnValue
        public @NotNull Builder sound(@NotNull AutoMessageSound sound) {
            Objects.requireNonNull(sound, "notice cannot be null");
            this.sound = sound;
            return this;
        }

        @Contract("_ -> this")
        @CheckReturnValue
        public @NotNull Builder requiredPermission(@Nullable String requiredPermission) {
            this.requiredPermission = requiredPermission;
            return this;
        }

        @Contract("_ -> this")
        @CheckReturnValue
        public @NotNull Builder requiredGroup(@Nullable String requiredGroup) {
            this.requiredGroup = requiredGroup;
            return this;
        }

        /**
         * Builds the {@link AutoMessageNotice} instance.
         *
         * @return new AutoMessageNotice instance
         * @throws IllegalStateException if notice is not set
         */
        @CheckReturnValue
        public @NotNull AutoMessageNotice build() {
            return new AutoMessageNotice(this.notices, this.sound, this.requiredPermission, this.requiredGroup);
        }
    }
}