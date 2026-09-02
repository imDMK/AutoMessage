plugins {
    `automessage-spigot-compat`
    `automessage-adventure-compat`
}

// Everything the Bukkit family shares: viewers, the event listener, metrics.
//
// A library, not a plugin. It carries no plugin.yml, because both Bukkit and Folia depend on it
// and a module with an entry point would put a second plugin.yml inside every jar that shaded it,
// leaving the server to pick one at random.
dependencies {
    api(project(":automessage-core"))

    // compileOnly, not compileOnlyApi: folia-api declares the same capability because it is a
    // fork of this, and exporting spigot-api would make every Folia build a capability conflict.
    compileOnly("org.spigotmc:spigot-api:${Versions.SPIGOT_API}")
    compileOnly("me.clip:placeholderapi:${Versions.PLACEHOLDER_API}")

    api("net.kyori:adventure-platform-bukkit:${Versions.KYORI_PLATFORM_BUKKIT}")
    api("org.bstats:bstats-bukkit:${Versions.BSTATS_BUKKIT}")
    api("dev.rollczi:litecommands-bukkit:${Versions.LITECOMMANDS}")
}
