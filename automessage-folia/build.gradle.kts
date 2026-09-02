plugins {
    `automessage-platform`
    `automessage-bukkit-plugin`
    `automessage-testing`

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
    runPort = 25595
    platformName = "Folia"
}

bukkit {
    main = "com.github.imdmk.automessage.folia.AutoMessageFoliaPlugin"
    foliaSupported = true
}

// Applying run-paper registers runServer for Paper; this repoints it at Folia so the module has
// one run task and it starts the server the module exists to test. run-paper's own runFolia is
// deliberately not registered - two names for one thing is what made this confusing.
tasks.named<xyz.jpenilla.runpaper.task.RunServer>("runServer") {
    description = "Run a Folia server for plugin testing."

    downloadsApiService.set(xyz.jpenilla.runtask.service.DownloadsAPIService.folia(project))
    version(Versions.FOLIA_RUN_MINECRAFT)
}
