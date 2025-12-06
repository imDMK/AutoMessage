apply(plugin = "java-library")

group = "com.github.imdmk.automessage"
version = "2.0.0"

subprojects {
    version = "2.0.0"
    apply(plugin = "java-library")

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
        maven("https://repo.eternalcode.pl/releases") // Eternalcode
        maven("https://storehouse.okaeri.eu/repository/maven-public/") // Okaeri
        maven("https://repo.panda-lang.org/releases") // Litecommands
    }

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
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
        options.release.set(21)
    }
}