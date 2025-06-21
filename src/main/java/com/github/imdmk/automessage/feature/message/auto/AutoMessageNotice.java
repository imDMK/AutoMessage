package com.github.imdmk.automessage.feature.message.auto;

import com.eternalcode.multification.notice.Notice;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Represents an auto-message notice with optional permission and group requirements.
 */
public class AutoMessageNotice {

    private final @NotNull Notice notice;
    private final @Nullable String requiredPermission;
    private final @Nullable String requiredGroup;

    private AutoMessageNotice(@NotNull Notice notice, @Nullable String requiredPermission, @Nullable String requiredGroup) {
        this.notice = notice;
        this.requiredPermission = requiredPermission;
        this.requiredGroup = requiredGroup;
    }

    /**
     * @return the associated {@link Notice}
     */
    public @NotNull Notice getNotice() {
        return this.notice;
    }

    /**
     * @return optional required permission
     */
    public @NotNull Optional<String> getRequiredPermission() {
        return Optional.ofNullable(this.requiredPermission);
    }

    /**
     * @return optional required group
     */
    public @NotNull Optional<String> getRequiredGroup() {
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

        private @Nullable Notice notice;
        private @Nullable String requiredPermission;
        private @Nullable String requiredGroup;

        @Contract("_ -> this")
        @CheckReturnValue
        public @NotNull Builder notice(@NotNull Notice notice) {
            this.notice = notice;
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
            if (this.notice == null) {
                throw new IllegalStateException("Notice must not be null");
            }

            return new AutoMessageNotice(this.notice, this.requiredPermission, this.requiredGroup);
        }
    }
}