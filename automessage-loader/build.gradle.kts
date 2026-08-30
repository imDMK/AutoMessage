import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `automessage-spigot-compat`

    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.gradleup.shadow") version "9.6.1"
    id("xyz.jpenilla.run-paper") version "3.1.0"
}

dependencies {
    implementation(project(":automessage-core"))
}

tasks.build {
    dependsOn(tasks.test)
    dependsOn(tasks.shadowJar)
}

tasks.withType<ShadowJar> {
    archiveFileName.set("AutoMessage v${project.version} (MC ${Versions.SUPPORTED_MINECRAFT_RANGE}).jar")

    // Shadow's transformers only see entries that reach them; with the Jar default of EXCLUDE a
    // second provider file of the same name would be dropped before mergeServiceFiles() runs.
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    mergeServiceFiles()

    exclude(
        "META-INF/*.SF",
        "META-INF/*.DSA",
        "META-INF/*.RSA",
        "module-info.class",
        "org/intellij/lang/annotations/**",
        "org/jetbrains/annotations/**"
    )

    val relocationPrefix = "com.github.imdmk.automessage.lib"
    listOf(
        "com.eternalcode.multification",
        "dev.rollczi.litecommands",
        "eu.okaeri.configs",
        "net.kyori",
        "org.bstats",
        "org.yaml.snakeyaml",
    ).forEach { pkg ->
        relocate(pkg, "$relocationPrefix.$pkg")
    }

    // No minimize(): Adventure reaches its CraftBukkit serializers and its ServiceLoader
    // providers reflectively, so the reachability analysis drops classes the plugin needs —
    // including JSONComponentSerializerProviderImpl, which META-INF/services still points at.
}

bukkit {
    name = "AutoMessage"
    version = project.version.toString()
    apiVersion = Versions.SPIGOT_API_VERSION
    softDepend = listOf("PlaceholderAPI")
    main = "com.github.imdmk.automessage.AutoMessagePluginLoader"
    author = "imDMK (dominiks8318@gmail.com)"
    description = "High-performance plugin for fully customizable automatic server-wide broadcasts."
    website = "https://github.com/imDMK/AutoMessage"
}

tasks {
    runServer {
        minecraftVersion("26.2")
    }
}
