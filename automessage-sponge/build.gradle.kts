import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
    `automessage-platform`
    `automessage-adventure-compat`

    id("org.spongepowered.gradle.plugin")
}

// Sponge.
//
// A full server, and the closest of the non-Bukkit platforms to one: it keeps vanilla statistics
// and player profiles, its worlds have identities, and it speaks Adventure natively - against
// 4.26.1, the very version this plugin pins. What it does not have is PlaceholderAPI, which is a
// Bukkit plugin, and that is the only capability the configuration written here leaves out.
dependencies {
    api(project(":automessage-core"))

    api("dev.rollczi:litecommands-sponge:${Versions.LITECOMMANDS}")
    api("org.bstats:bstats-sponge:${Versions.BSTATS_SPONGE}")
}

// Generates `META-INF/sponge_plugins.json`, which is what Sponge reads to find the entry point.
//
// Also contributes the `spongeapi` dependency, so it is not declared above: naming the version
// twice is how the metadata and the compile classpath come to disagree.
sponge {
    apiVersion(Versions.SPONGE_API)
    license("MIT")

    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }

    plugin("automessage") {
        displayName("AutoMessage")
        entrypoint("com.github.imdmk.automessage.sponge.AutoMessageSpongePlugin")
        description("High-performance plugin for fully customizable automatic server-wide broadcasts.")

        contributor("imDMK") {
            description("Author")
        }

        links {
            homepage("https://github.com/imDMK/AutoMessage")
            source("https://github.com/imDMK/AutoMessage")
            issues("https://github.com/imDMK/AutoMessage/issues")
        }

        dependency("spongeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
    }
}

automessagePlatform {
    platformName = "Sponge"

    // One API version is one Minecraft version here, so the jar says which rather than inheriting
    // the Bukkit family's range, which it has nothing to do with.
    supportedVersions = Versions.SPONGE_MINECRAFT

    // Sponge provides Adventure itself, against the very version this plugin pins.
    providedByPlatform("net.kyori")
}

