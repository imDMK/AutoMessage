plugins {
    `automessage-platform`
    `automessage-bukkit-plugin`

    id("xyz.jpenilla.run-paper")
}

// Spigot, Paper, Purpur and the rest of the Bukkit family. Not Folia - that has its own module.
dependencies {
    api(project(":automessage-bukkit-common"))

    compileOnly("org.spigotmc:spigot-api:${Versions.SPIGOT_API}")
}

automessagePlatform {
    platformName = "Bukkit"
}

bukkit {
    main = "com.github.imdmk.automessage.bukkit.AutoMessageBukkitPlugin"
}

tasks {
    runServer {
        minecraftVersion("26.2")
    }
}
