package com.github.imdmk.automessage.minestom;

import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;

// Minestom ships no permission system - a sender has an operator level and nothing else, which
// answers whether somebody is an administrator rather than who they are. The server author
// supplies the real answer; without one the platform declares no permission capability, so the
// rules that would need it are left out of the configuration instead of never matching.
@FunctionalInterface
public interface MinestomPermissions {

    int OPERATOR_LEVEL = 2;

    boolean has(CommandSender sender, String permission);

    // A stand-in, not a permission system: it cannot tell one node from another, so it is right
    // for gating the plugin's own commands and wrong for anything that asks who a player is.
    static MinestomPermissions operatorLevel() {
        return (sender, permission) -> !(sender instanceof Player player)
                || player.getPermissionLevel() >= OPERATOR_LEVEL;
    }
}
