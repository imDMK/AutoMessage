package com.github.imdmk.automessage.minestom;

import com.github.imdmk.automessage.AutoMessage;
import com.github.imdmk.automessage.command.CommandRegistrar;
import com.github.imdmk.automessage.logging.Slf4jPluginLogger;
import com.github.imdmk.automessage.platform.placeholder.ExternalPlaceholderResolver;
import com.github.imdmk.automessage.platform.viewer.ViewerRegistry;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.minestom.LiteMinestomFactory;
import dev.rollczi.litecommands.permission.PermissionResolver;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.network.ConnectionManager;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

public final class AutoMessageMinestom {

    private final AutoMessage automessage;
    private final LiteCommands<CommandSender> liteCommands;
    private final EventNode<Event> parent;
    private final EventNode<Event> events;

    private AutoMessageMinestom(
            AutoMessage automessage,
            LiteCommands<CommandSender> liteCommands,
            EventNode<Event> parent,
            EventNode<Event> events
    ) {
        this.automessage = automessage;
        this.liteCommands = liteCommands;
        this.parent = parent;
        this.events = events;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void shutdown() {
        parent.removeChild(events);
        liteCommands.unregister();
        automessage.shutdown();
    }

    public static final class Builder {

        private static final String EVENT_NODE_NAME = "automessage";
        private static final String DEFAULT_DIRECTORY = "automessage";

        private File dataDirectory = new File(DEFAULT_DIRECTORY);
        private @Nullable MinestomPermissions permissions;
        private @Nullable Logger logger;

        private Builder() {
        }

        public Builder dataDirectory(Path dataDirectory) {
            this.dataDirectory = dataDirectory.toFile();
            return this;
        }

        public Builder permissions(MinestomPermissions permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder logger(Logger logger) {
            this.logger = logger;
            return this;
        }

        public AutoMessageMinestom enable() {
            final boolean realPermissions = permissions != null;
            final MinestomPermissions resolved = realPermissions
                    ? permissions
                    : MinestomPermissions.operatorLevel();

            final ConnectionManager connections = MinecraftServer.getConnectionManager();
            final EventNode<Event> parent = MinecraftServer.getGlobalEventHandler();

            final MinestomTaskScheduler taskScheduler =
                    new MinestomTaskScheduler(MinecraftServer.getSchedulerManager());
            final ViewerRegistry viewers = new MinestomViewerRegistry(connections, resolved);

            final AutoMessage automessage = new AutoMessage(
                    new MinestomPlatform(taskScheduler, viewers, realPermissions),
                    new Slf4jPluginLogger(logger != null ? logger : LoggerFactory.getLogger(AutoMessage.NAME)),
                    dataDirectory,
                    // PlaceholderAPI is a Bukkit plugin; nothing here owns another plugin's %tokens%.
                    ExternalPlaceholderResolver.disabled()
            );

            final MinestomViewerFactory viewerFactory = new MinestomViewerFactory(resolved);

            // Its own node rather than listeners on the global one, so shutdown can take them all
            // back off again - which a server that restarts the plugin in-process needs.
            final EventNode<Event> node = EventNode.all(EVENT_NODE_NAME);
            new MinestomTriggerListener(connections, viewerFactory, automessage.triggerService())
                    .register(node);
            parent.addChild(node);

            final var commandBuilder = LiteMinestomFactory.builder();
            CommandRegistrar.configure(commandBuilder, automessage, viewerFactory);

            // LiteCommands has no permission binding for Minestom - there is nothing on the
            // platform for it to bind to - so without this every command would be open to anyone.
            commandBuilder.permissionResolver(PermissionResolver.createDefault(
                    CommandSender.class, resolved::has
            ));

            return new AutoMessageMinestom(automessage, commandBuilder.build(), parent, node);
        }
    }
}
