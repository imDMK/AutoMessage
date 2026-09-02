plugins {
    `automessage-platform`
    `automessage-adventure-compat`

    id("xyz.jpenilla.run-velocity")
}

// The Velocity proxy.
//
// Velocity speaks Adventure natively, so there is no platform bridge to shade here - a Player is
// already an Audience. What it does not have is worlds, playtime, or PlaceholderAPI, and the
// configuration it writes says so.
dependencies {
    api(project(":automessage-core"))
    api(project(":automessage-slf4j"))

    compileOnly("com.velocitypowered:velocity-api:${Versions.VELOCITY_API}")
    annotationProcessor("com.velocitypowered:velocity-api:${Versions.VELOCITY_API}")

    api("dev.rollczi:litecommands-velocity:${Versions.LITECOMMANDS}")
    api("org.bstats:bstats-velocity:${Versions.BSTATS_VELOCITY}")
}

automessagePlatform {
    platformName = "Velocity"

    // The proxy provides Adventure; a second copy under another package would make every
    // Component the plugin builds unusable there.
    providedByPlatform("net.kyori")
}

// Velocity reads the plugin's version from an annotation, which has to be a compile-time
// constant - so it is generated from the project version rather than typed in twice and left to
// drift.
val generateBuildConstants by tasks.registering {
    val output = layout.buildDirectory.dir("generated/sources/buildConstants")
    val pluginVersion = project.version.toString()

    inputs.property("version", pluginVersion)
    outputs.dir(output)

    doLast {
        val target = output.get().asFile.resolve("com/github/imdmk/automessage/velocity")
        target.mkdirs()
        target.resolve("BuildConstants.java").writeText(
            """
            package com.github.imdmk.automessage.velocity;

            // Generated from the Gradle project version - do not edit.
            public final class BuildConstants {

                public static final String VERSION = "$pluginVersion";

                private BuildConstants() {
                }
            }
            """.trimIndent() + "\n"
        )
    }
}

sourceSets.main {
    java.srcDir(generateBuildConstants)
}

// run-velocity calls its task runVelocity; this adds the runServer every other module answers to.
// Both are configured together so it does not matter which one gets started.
tasks.register<xyz.jpenilla.runvelocity.task.RunVelocity>("runServer") {
    description = "Run a Velocity proxy for plugin testing."
}

tasks.withType<xyz.jpenilla.runvelocity.task.RunVelocity>().configureEach {
    velocityVersion(Versions.VELOCITY_API)
}
