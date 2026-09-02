pluginManagement {
    repositories {
        gradlePluginPortal()

        // SpongeGradle, which generates the Sponge plugin metadata, is published here only.
        maven("https://repo.spongepowered.org/repository/maven-public/")

        // Fabric Loom, which is what turns a jar into a mod Minecraft can load.
        maven("https://maven.fabricmc.net/")
    }

    // Declared here so a module names a plugin without also naming a version - the same reason
    // library versions live in Versions.kt rather than in whichever module happened to need one
    // first. Shadow and plugin-yml are not here: they are applied by convention plugins, so their
    // versions live on buildSrc's classpath instead.
    plugins {
        id("xyz.jpenilla.run-paper") version "3.1.0"
        id("xyz.jpenilla.run-velocity") version "3.1.0"
        id("org.spongepowered.gradle.plugin") version "2.3.0"
        id("fabric-loom") version "1.17.20"
    }
}

rootProject.name = "AutoMessage"

include("automessage-api")
include("automessage-notice")
include("automessage-core")
include("automessage-slf4j")

include("automessage-bukkit-common")
include("automessage-bukkit")
include("automessage-folia")
include("automessage-sponge")
include("automessage-velocity")
include("automessage-minestom")
include("automessage-fabric")
