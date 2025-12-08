dependencies {
    compileOnlyApi("org.spigotmc:spigot-api:1.21.10-R0.1-SNAPSHOT")

    implementation("net.kyori:adventure-platform-bukkit:4.4.1")
    implementation("net.kyori:adventure-text-minimessage:4.25.0")

    implementation("com.eternalcode:multification-bukkit:1.2.2")
    implementation("com.eternalcode:multification-okaeri:1.2.2")

    api("eu.okaeri:okaeri-configs-yaml-snakeyaml:5.0.9")
    implementation("eu.okaeri:okaeri-configs-serdes-commons:5.0.5")

    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation("dev.rollczi:litecommands-bukkit:3.10.6")
    implementation("dev.rollczi:litecommands-annotations:3.10.6")

    testImplementation("org.junit.jupiter:junit-jupiter:6.0.1")
    testImplementation("org.assertj:assertj-core:3.25.2")
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.8.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}