import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.gradleup.shadow") version "9.2.1"
}

dependencies {
    implementation(project(":automessage-core"))
}

tasks.build {
    dependsOn(tasks.test)
    dependsOn(tasks.shadowJar)
}

tasks.withType<ShadowJar> {
    archiveFileName.set("AutoMessage v${project.version} (MC 1.21.x).jar")

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

    minimize()
}

bukkit {
    name = "AutoMessage"
    version = project.version.toString()
    apiVersion = "1.21"
    main = "com.github.imdmk.automessage.AutoMessagePluginLoader"
    author = "imDMK (dominiks8318@gmail.com)"
    description = "High-performance plugin for fully customizable automatic server-wide broadcasts."
    website = "https://github.com/imDMK/AutoMessage"
}