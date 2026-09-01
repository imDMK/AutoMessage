group = "com.github.imdmk.automessage"
version = "2.0.1"

subprojects {
    version = rootProject.version
    apply(plugin = "java-library")

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
        maven("https://repo.eternalcode.pl/releases") // Eternalcode
        maven("https://storehouse.okaeri.eu/repository/maven-public/") // Okaeri
        maven("https://repo.panda-lang.org/releases") // Litecommands
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") // PlaceholderAPI
        maven("https://repo.spongepowered.org/repository/maven-public/") // Sponge
        maven("https://repo.papermc.io/repository/maven-public/") {
            // Folia publishes Gradle Module Metadata and no POM, which Gradle does not look for
            // unless told to - without this the dependency simply fails to resolve.
            metadataSources {
                gradleMetadata()
                mavenPom()
                artifact()
            }
        }
    }

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(Versions.JAVA_TOOLCHAIN))
        withJavadocJar()
        withSourcesJar()
    }

    tasks.withType<Javadoc>().configureEach {
        isFailOnError = false

        val opts = options as StandardJavadocDocletOptions
        opts.addStringOption("Xdoclint:none", "-quiet")
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked", "-parameters"))
        options.encoding = "UTF-8"
        options.release.set(Versions.JAVA_RELEASE)
    }
}

// Every jar a server administrator would install, in one place.
//
// Collected through a configuration each platform module offers rather than by naming tasks from
// here: the shipped file is shadowJar's output on most platforms and remapJar's on Fabric, and a
// module knows which of those it is far better than the root does.
val platformJars: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

subprojects {
    // Fires when the module applies the convention, so adding a platform means adding it to
    // settings.gradle.kts and nowhere else.
    plugins.withId("automessage-platform") {
        val modulePath = path

        rootProject.dependencies.add(
            platformJars.name,
            rootProject.dependencies.project(modulePath, "platformJar")
        )
    }
}

val dist by tasks.registering(Sync::class) {
    group = "distribution"
    description = "Runs every check and collects the jar for each platform into build/dist."

    // Sync rather than Copy: a jar left over from an older version would otherwise sit in the
    // folder looking exactly as installable as the current one.
    from(platformJars)
    into(layout.buildDirectory.dir("dist"))

    // And always run, because up-to-date is the wrong answer for this one. Gradle skipped it for
    // a folder that had a stale jar dropped into it, which is precisely the state the sync exists
    // to correct. Everything expensive - compiling, testing - is upstream of here and still
    // skips; this is a copy of six files.
    outputs.upToDateWhen { false }

    dependsOn(subprojects.map { "${it.path}:check" })

    doLast {
        val target = destinationDir
        logger.lifecycle("")
        logger.lifecycle("Platform jars in ${target.relativeTo(rootDir)}:")

        target.listFiles()
            ?.sortedBy { it.name }
            ?.forEach { logger.lifecycle("  %-46s %,d KB".format(it.name, it.length() / 1024)) }
    }
}
