plugins {
    `automessage-adventure-compat`
    `automessage-testing`
}

dependencies {
    // Core decides when a message is sent and to whom; the notice module decides what and how.
    api(project(":automessage-api"))
    api(project(":automessage-notice"))

    implementation("net.kyori:adventure-text-minimessage:${Versions.KYORI_ADVENTURE}")

    // Flattens a rendered component to the words a Discord reader actually sees.
    implementation("net.kyori:adventure-text-serializer-plain:${Versions.KYORI_ADVENTURE}")

    api("eu.okaeri:okaeri-configs-yaml-snakeyaml:${Versions.OKAERI_CONFIGS}")
    implementation("eu.okaeri:okaeri-configs-serdes-commons:${Versions.OKAERI_CONFIGS}")

    // The commands live here, not in the platform modules: they take a Viewer rather than a
    // sender, so nothing in them names a server. Each platform adds only its own binding.
    api("dev.rollczi:litecommands-annotations:${Versions.LITECOMMANDS}")
    api("dev.rollczi:litecommands-framework:${Versions.LITECOMMANDS}")
}

