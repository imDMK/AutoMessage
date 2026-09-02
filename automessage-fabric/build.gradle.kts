plugins {
    `automessage-platform`
    `automessage-testing`

    id("fabric-loom")
}

// Fabric, and with it Quilt - whose loader reads `fabric.mod.json` and loads a Fabric mod as it
// is, so there is one jar rather than two identical ones.
//
// Pinned to Minecraft 1.21.11 by the ecosystem rather than by preference: it is the last version
// Fabric publishes intermediary and Yarn mappings for, and LiteCommands' Fabric binding is
// compiled against intermediary names, which 26.x no longer has.
//
// Nothing another mod already provides is shaded here. LiteCommands and Adventure both arrive as
// mods carrying their own libraries inside them, so shading a second copy - let alone a relocated
// one - would leave the plugin building Components nothing renders and calling into a framework
// nobody registered. Only okaeri and SnakeYAML, which no mod provides, travel inside this jar.
val shade: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true

    // Provided by the mods below, in the exact versions they were built against.
    exclude(group = "net.kyori")
    exclude(group = "dev.rollczi")
    exclude(group = "org.slf4j")
}

dependencies {
    api(project(":automessage-core"))
    api(project(":automessage-slf4j"))

    shade(project(":automessage-core"))
    shade(project(":automessage-slf4j"))

    minecraft("com.mojang:minecraft:${Versions.FABRIC_MINECRAFT}")
    mappings("net.fabricmc:yarn:${Versions.FABRIC_YARN}:v2")

    modImplementation("net.fabricmc:fabric-loader:${Versions.FABRIC_LOADER}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${Versions.FABRIC_API}")

    // Both are bundled, so a server administrator installs one jar rather than three.
    modImplementation("net.kyori:adventure-platform-fabric:${Versions.ADVENTURE_PLATFORM_FABRIC}")
    include("net.kyori:adventure-platform-fabric:${Versions.ADVENTURE_PLATFORM_FABRIC}")

    modImplementation("dev.rollczi:litecommands-fabric:${Versions.LITECOMMANDS}")
    include("dev.rollczi:litecommands-fabric:${Versions.LITECOMMANDS}")

}

// The mod metadata names versions the build already knows; they are filled in rather than typed twice.
tasks.processResources {
    val properties = mapOf(
        "version" to project.version.toString(),
        "loader_version" to Versions.FABRIC_LOADER,
        "minecraft_version" to Versions.FABRIC_MINECRAFT,
        "java_version" to Versions.JAVA_RELEASE.toString()
    )

    inputs.properties(properties)

    filesMatching("fabric.mod.json") {
        expand(properties)
    }
}

automessagePlatform {
    runPort = 25597
    platformName = "Fabric"
    supportedVersions = Versions.FABRIC_MINECRAFT

    // Nothing another mod already provides is shaded here: LiteCommands and Adventure both arrive
    // as mods carrying their own libraries inside them, so a second, relocated copy would leave
    // the plugin building Components nothing renders and calling a framework nobody registered.
    providedByPlatform("net.kyori", "dev.rollczi", "org.slf4j")

    // remapJar has the last word, so shadowJar's output is a step, not the product.
    shadowIsIntermediate = true

    shadowJar {
        configurations = listOf(shade)
    }
}

// The plugin is compiled against Adventure ${Versions.KYORI_ADVENTURE} but this mod runs on the
// ${Versions.ADVENTURE_FABRIC} that adventure-platform-fabric carries inside it. That gap is
// deliberate - see Versions.ADVENTURE_PLATFORM_FABRIC - and this is what keeps it honest rather
// than hopeful: every Adventure member the shipped bytecode calls is resolved against the jars
// the loader will really provide.
val adventureProvided: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    listOf(
        "adventure-api",
        "adventure-key",
        "adventure-text-minimessage",
        "adventure-text-serializer-plain",
    ).forEach { module ->
        adventureProvided("net.kyori:$module:${Versions.ADVENTURE_FABRIC}")
    }
}

val checkAdventureLinkage = tasks.register<CheckLinkageTask>("checkAdventureLinkage") {

    description = "Verifies the shipped mod links against the Adventure adventure-platform-fabric " +
            "actually bundles."
    group = LifecycleBasePlugin.VERIFICATION_GROUP

    // The shaded jar, not the class directory: this is the bytecode that ships, core and all.
    subjects.from(tasks.shadowJar.flatMap { it.archiveFile })
    provided.from(adventureProvided)

    packagePrefix.set("net/kyori/adventure/")

    // The Fabric bridge is versioned with the platform, not with Adventure, and is published
    // against different mappings again - it is the library underneath that is in question here.
    ignoredPrefixes.set(setOf("net/kyori/adventure/platform/"))
    providedDescription.set("Adventure ${Versions.ADVENTURE_FABRIC}")
    advice.set(
        "Either adventure-platform-fabric has a release on Adventure ${Versions.KYORI_ADVENTURE} " +
            "for a Minecraft the Fabric command binding can follow, or the plugin has to stop " +
            "calling what the older line does not have."
    )

    report.set(layout.buildDirectory.file("reports/linkage/adventure.txt"))
}

tasks.named("check") {
    dependsOn(checkAdventureLinkage)
}

// Loom remaps the mod from the mappings it was written against into the namespace the loader
// expects, and it has to be the shaded jar it remaps - otherwise the shipped mod is missing
// everything the plugin depends on.
tasks.remapJar {
    inputFile.set(tasks.shadowJar.flatMap { it.archiveFile })
    dependsOn(tasks.shadowJar)

    archiveFileName.set(automessagePlatform.artifactFileName())
}
