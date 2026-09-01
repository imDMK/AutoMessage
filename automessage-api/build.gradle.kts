plugins {
    `automessage-testing`
}

// What the core needs from whatever server it runs on: interfaces only, and only ones a platform
// can be expected to implement. Depends on Adventure, because an audience is how a message reaches
// a reader everywhere, and on nothing else at all - no server API, not even Bukkit's.
dependencies {
    api("net.kyori:adventure-api:${Versions.KYORI_ADVENTURE}")
}
