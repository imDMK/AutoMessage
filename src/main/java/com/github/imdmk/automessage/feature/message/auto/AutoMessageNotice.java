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

    private final @NotNull String name;
    private final @NotNull List<Notice> notices;

    private final @Nullable AutoMessageSound sound;
    private final @Nullable String requiredPermission;
    private final @Nullable String requiredGroup;

    private final boolean ignoreAdmins;

    private AutoMessageNotice(
            @NotNull String name,
            @NotNull List<Notice> notices,
            @Nullable AutoMessageSound sound,
            @Nullable String requiredPermission,
            @Nullable String requiredGroup,
            boolean ignoreAdmins
    ) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.notices = Objects.requireNonNull(notices, "notice cannot be null");
        this.sound = sound;
        this.requiredPermission = requiredPermission;
        this.requiredGroup = requiredGroup;
        this.ignoreAdmins = ignoreAdmins;
    }

    /**
     * @return the associated AutoMessageNotice name
     */
    public @NotNull String getName() {
        return this.name;
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
     * @return ignoreAdmins boolean
     */
    public boolean isIgnoreAdmins() {
        return this.ignoreAdmins;
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

        private String name;
        private List<Notice> notices = new ArrayList<>();

        private AutoMessageSound sound;
        private String requiredPermission;
        private String requiredGroup;
        private boolean ignoreAdmins;

        @Contract("_ -> this")
        @CheckReturnValue
        public @NotNull Builder name(@NotNull String name) {
            Objects.requireNonNull(name, "name cannot be null");
            this.name = name;
            return this;
        }

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
        public @NotNull Builder sound(@Nullable AutoMessageSound sound) {
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

        @Contract("_ -> this")
        @CheckReturnValue
        public @NotNull Builder ignoreAdmins(boolean ignore) {
            this.ignoreAdmins = ignore;
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
            return new AutoMessageNotice(this.name, this.notices, this.sound, this.requiredPermission, this.requiredGroup, this.ignoreAdmins);
        }
    }
}