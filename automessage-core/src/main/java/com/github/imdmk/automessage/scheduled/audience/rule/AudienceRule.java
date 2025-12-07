package com.github.imdmk.automessage.scheduled.audience.rule;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a rule used to determine whether a player is eligible
 * to receive a particular scheduled message.
 *
 * <p>Each rule implements a single boolean check and is composable.
 * The dispatcher evaluates all rules attached to a message; if any rule
 * returns {@code false}, the message is not sent to the player.</p>
 *
 * <p>This interface is sealed: only built-in rule types are permitted.</p>
 */
public sealed interface AudienceRule
        permits AudiencePermissionRule, AudienceGroupRule {

    /**
     * Creates a rule that allows only players belonging to a specific group.
     *
     * @param group the required group identifier (not null)
     * @return a new group-based audience rule
     */
    static @NotNull AudienceGroupRule group(@NotNull String group) {
        return new AudienceGroupRule(group);
    }

    /**
     * Creates a rule that allows only players having a specific permission node.
     *
     * @param permission the required permission (not null)
     * @return a new permission-based audience rule
     */
    static @NotNull AudiencePermissionRule permission(@NotNull String permission) {
        return new AudiencePermissionRule(permission);
    }

    /**
     * Evaluates whether the player satisfies this rule.
     *
     * @param player the player being evaluated (never null)
     * @return true if the rule allows the player; false otherwise
     */
    boolean test(@NotNull Player player);

    /**
     * Enumeration of supported audience rule types.
     */
    enum Type {
        PERMISSION,
        GROUP
    }

    /**
     * Returns the type of this rule.
     *
     * @return the rule type (never null)
     */
    @NotNull Type type();
}
