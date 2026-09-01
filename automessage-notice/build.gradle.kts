plugins {
    `automessage-testing`
}

// The message vocabulary: what an announcement can be, how it is written in YAML, and how it
// reaches a reader. No server API of any kind belongs here - that is what lets every platform
// share one implementation, and the moment this module learns about a platform they all inherit it.
dependencies {
    api("net.kyori:adventure-api:${Versions.KYORI_ADVENTURE}")
    api("net.kyori:adventure-text-minimessage:${Versions.KYORI_ADVENTURE}")
    api("net.kyori:adventure-text-serializer-plain:${Versions.KYORI_ADVENTURE}")

    api("eu.okaeri:okaeri-configs-yaml-snakeyaml:${Versions.OKAERI_CONFIGS}")
}
