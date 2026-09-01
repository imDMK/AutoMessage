plugins {
    `automessage-platform`
    `automessage-adventure-compat`
    `automessage-testing`
}

// Minestom.
//
// Not a server with a plugin folder: a library the server author writes a `main` around. So this
// module ships a jar to put on a classpath and a class to call, not something a platform loads -
// see AutoMessageMinestom.
//
// Pinned deliberately, and for the same reason Velocity is pinned to 3.x: this is the last
// Minestom release built against Adventure 4.26.1. Everything newer moved to Adventure 5, which
// `checkAdventureAlignment` exists to catch - adventure-platform-bukkit still links against types
// 5.x removed, so the whole stack stays on 4.x until it does not.
dependencies {
    api(project(":automessage-core"))
    api(project(":automessage-slf4j"))

    compileOnly("net.minestom:minestom:${Versions.MINESTOM}")

    api("dev.rollczi:litecommands-minestom:${Versions.LITECOMMANDS_MINESTOM}")

    testImplementation("net.minestom:minestom:${Versions.MINESTOM}")
}

// LiteCommands publishes no Minestom binding past 3.10.9, so this jar carries an older binding on
// a newer framework - Gradle resolves the framework up to the version the core is compiled
// against, and only the binding stays behind.
//
// The alternative was to drag the whole stack down to 3.10.9, which would have run the entire core
// against a framework it was not compiled against to spare one small jar. `checkLiteCommandsBinding`
// is what makes the smaller risk a measured one rather than a hope: it resolves every LiteCommands
// member the binding calls against the framework that actually lands on the classpath.
val checkLiteCommandsBinding = tasks.register<CheckLinkageTask>("checkLiteCommandsBinding") {

    description = "Verifies the held-back LiteCommands Minestom binding links against the " +
            "framework version resolution actually settles on."
    group = LifecycleBasePlugin.VERIFICATION_GROUP

    val runtimeClasspath = configurations.named("runtimeClasspath")

    subjects.from(runtimeClasspath.map { it.filter { file -> file.name.startsWith("litecommands-minestom-") } })
    provided.from(runtimeClasspath.map { it.filter { file -> file.name.startsWith("litecommands-") &&
            !file.name.startsWith("litecommands-minestom-") } })

    packagePrefix.set("dev/rollczi/litecommands/")
    providedDescription.set("LiteCommands ${Versions.LITECOMMANDS}")
    advice.set(
        "Either the Minestom binding has caught up and Versions.LITECOMMANDS_MINESTOM can be " +
            "bumped, or the framework has to stay where the binding can follow it."
    )

    report.set(layout.buildDirectory.file("reports/linkage/litecommands.txt"))
}

tasks.named("check") {
    dependsOn(checkLiteCommandsBinding)
}

// The one module that is not built for Java 21 - see Versions.MINESTOM_JAVA_RELEASE. Minestom's
// own bytecode is 25, so this is the level a Minestom server already runs at.
tasks.withType<JavaCompile>().configureEach {
    options.release.set(Versions.MINESTOM_JAVA_RELEASE)
}

automessagePlatform {
    platformName = "Minestom"

    // A Minestom build speaks exactly one protocol; naming the Bukkit range here would be a
    // compatibility claim nobody made.
    supportedVersions = Versions.MINESTOM_MINECRAFT

    providedByPlatform("net.kyori", "org.slf4j")
}
