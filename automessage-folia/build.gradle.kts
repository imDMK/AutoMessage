plugins {
    `automessage-platform`
    `automessage-bukkit-plugin`

    id("xyz.jpenilla.run-paper")
}

// Folia: Bukkit's API, but no global scheduler - the server is regionised and every task has to
// say which region it belongs to. Everything except the scheduler and the entry point is shared
// with the Bukkit module, which is why this depends on it rather than copying it.
dependencies {
    api(project(":automessage-bukkit-common"))

    compileOnly("dev.folia:folia-api:${Versions.FOLIA_API}")
    compileOnly("me.clip:placeholderapi:${Versions.PLACEHOLDER_API}")

    api("dev.rollczi:litecommands-folia:${Versions.LITECOMMANDS}")
}

automessagePlatform {
    platformName = "Folia"
}

bukkit {
    main = "com.github.imdmk.automessage.folia.AutoMessageFoliaPlugin"
    foliaSupported = true
}

// Folia is not a Paper build with a flag, so run-paper registers a task of its own for it - which
// is the only way to exercise the regionised schedulers this module exists for.
runPaper.folia.registerTask {
    minecraftVersion("26.2")
}
