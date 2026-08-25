dependencies {
    compileOnlyApi("org.spigotmc:spigot-api:26.2-R0.1-SNAPSHOT")

    implementation("net.kyori:adventure-platform-bukkit:4.4.1")
    implementation("net.kyori:adventure-text-minimessage:4.26.1")

    implementation("com.eternalcode:multification-bukkit:1.2.4")
    implementation("com.eternalcode:multification-okaeri:1.2.4")

    api("eu.okaeri:okaeri-configs-yaml-snakeyaml:5.0.13")
    implementation("eu.okaeri:okaeri-configs-serdes-commons:5.0.13")

    implementation("org.bstats:bstats-bukkit:3.2.1")
    implementation("dev.rollczi:litecommands-bukkit:3.11.0")
    implementation("dev.rollczi:litecommands-annotations:3.11.0")

    testImplementation("org.junit.jupiter:junit-jupiter:6.1.3")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.mockito:mockito-core:5.23.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.23.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}