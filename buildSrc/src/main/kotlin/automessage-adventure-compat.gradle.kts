plugins {
    `java-library`
}

// `adventure-platform-bukkit` is still published against the Adventure 4.x API and links
// against types 5.x removed — `net.kyori.adventure.audience.MessageType` among them. Nothing
// in the build fails when a 5.x module is pulled in: the jar shades cleanly and only dies on
// the first message sent, with a NoClassDefFoundError.
//
// A bump of any single Adventure module drags the rest of the line along with it, so this
// check asserts the whole shaded stack stays on the major line the platform expects.
abstract class CheckAdventureAlignmentTask : DefaultTask() {

    @get:Input
    abstract val modules: MapProperty<String, String>

    @get:Input
    abstract val expectedMajor: Property<String>

    @get:OutputFile
    abstract val report: RegularFileProperty

    @TaskAction
    fun check() {

        val major = expectedMajor.get()
        val resolved = modules.get().toSortedMap()

        val problems = resolved
            .filterValues { version -> version.substringBefore('.') != major }
            .map { (module, version) -> "$module resolves to $version, expected the ${major}.x line" }

        val file = report.get().asFile
        file.parentFile.mkdirs()
        file.writeText(
            buildString {
                appendLine("Adventure major line: $major.x")
                appendLine()
                resolved.forEach { (module, version) -> appendLine("$module:$version") }
                appendLine()
                if (problems.isEmpty()) appendLine("OK") else problems.forEach { appendLine(it) }
            }
        )

        if (problems.isNotEmpty()) {
            throw GradleException(
                problems.joinToString(
                    prefix = "Adventure stack is not aligned with adventure-platform-bukkit:\n  - ",
                    separator = "\n  - ",
                    postfix = "\n\nadventure-platform-bukkit is built against Adventure $major.x and links against " +
                            "types later lines removed. Keep every net.kyori:adventure-* module on $major.x until " +
                            "the platform modules publish a release for the newer line."
                )
            )
        }
    }
}

val checkAdventureAlignment = tasks.register<CheckAdventureAlignmentTask>("checkAdventureAlignment") {

    description = "Verifies every shaded net.kyori:adventure-* module stays on the major line " +
            "adventure-platform-bukkit is built against."
    group = LifecycleBasePlugin.VERIFICATION_GROUP

    val runtimeClasspath = configurations.named("runtimeClasspath")

    modules.set(
        runtimeClasspath.map { configuration ->
            configuration.incoming.resolutionResult.allComponents
                .mapNotNull { it.moduleVersion }
                .filter { it.group == "net.kyori" && it.name.startsWith("adventure-") }
                .filterNot { it.name.startsWith("adventure-platform-") }
                .associate { "${it.group}:${it.name}" to it.version }
        }
    )

    expectedMajor.set(Versions.KYORI_ADVENTURE_MAJOR)
    report.set(layout.buildDirectory.file("reports/adventure-compat/${project.name}.txt"))
}

tasks.named("check") {
    dependsOn(checkAdventureAlignment)
}
