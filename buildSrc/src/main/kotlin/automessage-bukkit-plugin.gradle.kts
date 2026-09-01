import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    `java-library`
    id("net.minecrell.plugin-yml.bukkit")
}

// The plugin.yml fields Bukkit and Folia share.
//
// Only the entry point and the Folia flag actually differ between the two, and everything else was
// written out twice - which is how one jar ends up describing itself differently from the other.
extensions.configure<BukkitPluginDescription> {
    name = "AutoMessage"
    version = project.version.toString()
    apiVersion = Versions.SPIGOT_API_VERSION
    softDepend = listOf("PlaceholderAPI")
    author = "imDMK (dominiks8318@gmail.com)"
    description = "High-performance plugin for fully customizable automatic server-wide broadcasts."
    website = "https://github.com/imDMK/AutoMessage"
}
