plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("org.ow2.asm:asm:9.10.1")

    // Used by the automessage-platform convention, which every platform module applies.
    implementation("com.gradleup.shadow:shadow-gradle-plugin:9.6.1")

    // Used by the automessage-bukkit-plugin convention, which writes plugin.yml for Bukkit
    // and Folia.
    implementation("net.minecrell.plugin-yml.bukkit:net.minecrell.plugin-yml.bukkit.gradle.plugin:0.6.0")
}

sourceSets {
    main {
        java.setSrcDirs(emptyList<String>())
        groovy.setSrcDirs(emptyList<String>())
        resources.setSrcDirs(emptyList<String>())
    }
    test {
        java.setSrcDirs(emptyList<String>())
        kotlin.setSrcDirs(emptyList<String>())
        groovy.setSrcDirs(emptyList<String>())
        resources.setSrcDirs(emptyList<String>())
    }
}
