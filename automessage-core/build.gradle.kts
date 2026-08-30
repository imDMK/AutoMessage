plugins {
    `automessage-spigot-compat`
    `automessage-adventure-compat`
}

dependencies {
    compileOnlyApi("org.spigotmc:spigot-api:${Versions.SPIGOT_API}")

    implementation("net.kyori:adventure-platform-bukkit:${Versions.KYORI_PLATFORM_BUKKIT}")
    implementation("net.kyori:adventure-text-minimessage:${Versions.KYORI_ADVENTURE}")

    // Flattens a rendered component to the words a Discord reader actually sees.
    implementation("net.kyori:adventure-text-serializer-plain:${Versions.KYORI_ADVENTURE}")

    implementation("com.eternalcode:multification-bukkit:${Versions.MULTIFICATION}")
    implementation("com.eternalcode:multification-okaeri:${Versions.MULTIFICATION}")

    api("eu.okaeri:okaeri-configs-yaml-snakeyaml:${Versions.OKAERI_CONFIGS}")
    implementation("eu.okaeri:okaeri-configs-serdes-commons:${Versions.OKAERI_CONFIGS}")

    implementation("org.bstats:bstats-bukkit:${Versions.BSTATS_BUKKIT}")
    implementation("dev.rollczi:litecommands-bukkit:${Versions.LITECOMMANDS}")
    implementation("dev.rollczi:litecommands-annotations:${Versions.LITECOMMANDS}")

    // compileOnlyApi keeps spigot-api out of the shaded jar, but it also keeps it off the test
    // runtime classpath - and tests that touch Bukkit types need it there to load at all.
    testImplementation("org.spigotmc:spigot-api:${Versions.SPIGOT_API}")

    testImplementation("org.junit.jupiter:junit-jupiter:${Versions.JUNIT}")
    testImplementation("org.assertj:assertj-core:${Versions.ASSERTJ}")
    testImplementation("org.mockito:mockito-core:${Versions.MOCKITO}")
    testImplementation("org.mockito:mockito-junit-jupiter:${Versions.MOCKITO}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
