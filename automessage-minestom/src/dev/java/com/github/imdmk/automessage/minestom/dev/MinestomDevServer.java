package com.github.imdmk.automessage.minestom.dev;

import com.github.imdmk.automessage.minestom.AutoMessageMinestom;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;

import java.nio.file.Path;

// A Minestom server that exists only to try the plugin on.
//
// Every other platform is a program you drop a jar into; Minestom is a library you write the
// program around, so there is nothing to run until somebody writes this. It is the smallest server
// that can hold a player: one flat instance, one spawn point, and AutoMessage started the way the
// README tells an embedder to start it.
public final class MinestomDevServer {

    private static final int PORT = 25594;
    private static final int GROUND = 40;

    private MinestomDevServer() {
    }

    public static void main(String[] args) {
        final MinecraftServer server = MinecraftServer.init();

        final InstanceContainer instance = MinecraftServer.getInstanceManager().createInstanceContainer();
        instance.setGenerator(unit -> unit.modifier().fillHeight(0, GROUND, Block.GRASS_BLOCK));

        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(instance);
            event.getPlayer().setRespawnPoint(new Pos(0, GROUND, 0));
        });

        final AutoMessageMinestom automessage = AutoMessageMinestom.builder()
                .dataDirectory(Path.of("automessage"))
                .enable();

        Runtime.getRuntime().addShutdownHook(new Thread(automessage::shutdown));

        server.start("0.0.0.0", PORT);
        MinecraftServer.LOGGER.info("AutoMessage development server listening on {}", PORT);
    }
}
