import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    id("com.gradleup.shadow")
}

// Shared by every platform module.
//
// Each platform ships its own jar - a Bukkit server has no use for the Velocity build and cannot
// load it - so the shadow setup lives here rather than being repeated per module with room to
// drift.
open class PlatformExtension(private val project: Project) {

    // Shown in the artifact name, e.g. "Bukkit" or "Velocity".
    var platformName: String = ""

    // Server versions the jar is built for, shown in the artifact name.
    var supportedVersions: String = Versions.SUPPORTED_MINECRAFT_RANGE

    // Whether the shadowed jar is the artifact, or a step towards it.
    //
    // Fabric has one more stage after shading - Loom remaps the mod back into the namespace the
    // loader expects - so there the shipped file is remapJar's output, and shadowJar's is an
    // intermediate that should not be mistaken for it in `build/libs`.
    var shadowIsIntermediate: Boolean = false

    /**
     * Port the module's runServer listens on, or zero where the platform is configured elsewhere.
     *
     * One per platform, so several can run at once and none of them lands on 25565 - which on a
     * developer's own machine is usually already taken by the server they are actually playing on.
     */
    var runPort: Int = 0

    // Bundled third-party packages, moved out of the way so two plugins shading different
    // versions of the same library cannot collide inside one server.
    val relocated: MutableList<String> = mutableListOf(
        "dev.rollczi.litecommands",
        "eu.okaeri.configs",
        "net.kyori",
        "org.bstats",
        "org.yaml.snakeyaml",
    )

    internal val provided: MutableList<String> = mutableListOf()

    // Libraries the platform brings itself: neither shaded nor relocated.
    //
    // Adventure is the one that matters. Where a platform speaks it natively, a shaded copy under
    // another package is not the one it renders, so every Component the plugin builds becomes a
    // silent no-op - which is why this is stated per module rather than left to a list somebody
    // has to remember to prune.
    fun providedByPlatform(vararg groups: String) {
        provided += groups
        relocated.removeIf { pkg -> groups.any { pkg == it || pkg.startsWith("$it.") } }
    }

    // The name the shipped jar carries, wherever it is finally produced.
    fun artifactFileName(): String =
        "AutoMessage-$platformName v${project.version} ($supportedVersions).jar"

    internal var shadowAction: Action<ShadowJar>? = null

    fun shadowJar(action: Action<ShadowJar>) {
        shadowAction = action
    }
}

val platform = extensions.create("automessagePlatform", PlatformExtension::class.java, project)

// What this module ships, offered to whoever asks for it - which is the root project's `dist`.
// Named rather than guessed at, because the shipped file is not the same task everywhere: Fabric
// remaps its shaded jar afterwards, so shadowJar's output there is a step, not the product.
val platformJar: Configuration by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}

// Minecraft rewrites server.properties on every start, filling in every default it knows - which
// silently puts the port back to 25565. Re-applying the two settings that matter before each run
// is what keeps the task usable a second time, and it leaves every other line the developer or the
// server wrote alone.
afterEvaluate {
    if (platform.runPort != 0 && tasks.names.contains("runServer")) {
        tasks.named("runServer") {
            doFirst {
                val runDirectory = layout.projectDirectory.dir("run").asFile
                runDirectory.mkdirs()

                val properties = runDirectory.resolve("server.properties")
                val settings = mapOf(
                    "server-port" to platform.runPort.toString(),
                    "online-mode" to "false",
                )

                val kept = properties.takeIf { it.isFile }
                    ?.readLines()
                    ?.filterNot { line -> settings.keys.any { line.startsWith("$it=") } }
                    ?: emptyList()

                properties.writeText((kept + settings.map { (k, v) -> "$k=$v" }).joinToString("\n") + "\n")
            }
        }
    }
}

afterEvaluate {
    val shipped = tasks.named(if (platform.shadowIsIntermediate) "remapJar" else "shadowJar")

    artifacts.add(platformJar.name, shipped.map { (it as AbstractArchiveTask).archiveFile.get() }) {
        builtBy(shipped)
    }

    tasks.withType<ShadowJar>().configureEach {
        archiveFileName.set(
            if (platform.shadowIsIntermediate) "${project.name}-shaded.jar" else platform.artifactFileName()
        )

        // An intermediate is kept out of build/libs, where everything is something to ship.
        if (platform.shadowIsIntermediate) {
            destinationDirectory.set(layout.buildDirectory.dir("shaded"))
        }

        // Shadow's transformers only see entries that reach them; with the Jar default of EXCLUDE a
        // second provider file of the same name would be dropped before mergeServiceFiles() runs.
        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        mergeServiceFiles()

        exclude(
            "META-INF/*.SF",
            "META-INF/*.DSA",
            "META-INF/*.RSA",
            "module-info.class",
            "org/intellij/lang/annotations/**",
            "org/jetbrains/annotations/**"
        )

        // No minimize(): Adventure reaches its serializers and its ServiceLoader providers
        // reflectively, so the reachability analysis drops classes the plugin needs.

        val relocationPrefix = "com.github.imdmk.automessage.lib"
        platform.relocated.forEach { pkg -> relocate(pkg, "$relocationPrefix.$pkg") }

        dependencies {
            platform.provided.forEach { group -> exclude(dependency("$group:.*")) }
        }

        platform.shadowAction?.execute(this)
    }
}
