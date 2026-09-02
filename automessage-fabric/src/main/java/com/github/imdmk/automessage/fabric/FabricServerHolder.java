package com.github.imdmk.automessage.fabric;

import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

// The server, once there is one.
//
// The mod is built before the server exists, because Fabric asks for commands while it is still
// starting - registering them any later means they are simply not there. Everything that genuinely
// needs a MinecraftServer is reached through here instead, and answers as though nobody were
// online until it arrives.
final class FabricServerHolder {

    private volatile @Nullable MinecraftServer server;
    private volatile @Nullable MinecraftServerAudiences audiences;

    void started(MinecraftServer server) {
        this.audiences = MinecraftServerAudiences.of(server);
        this.server = server;
    }

    void stopped() {
        this.server = null;
        this.audiences = null;
    }

    @Nullable
    MinecraftServer server() {
        return server;
    }

    @Nullable
    MinecraftServerAudiences audiences() {
        return audiences;
    }
}
