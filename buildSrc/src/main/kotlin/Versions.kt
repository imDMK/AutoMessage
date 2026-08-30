object Versions {

    /** JDK the project is built with. */
    const val JAVA_TOOLCHAIN = 21

    /**
     * Bytecode level of the shipped plugin, and therefore the oldest JVM a server may
     * run it on. Kept at 21 because that is what Minecraft 1.21 itself requires — a
     * higher target would lock the plugin out of the low end of the supported range.
     */
    const val JAVA_RELEASE = 21

    /** Spigot API the plugin is compiled against (newest supported server). */
    const val SPIGOT_API = "26.2-R0.1-SNAPSHOT"

    /** Oldest supported Spigot API, verified by the `compileJavaSpigotMin` task. */
    const val SPIGOT_API_MIN = "1.21-R0.1-SNAPSHOT"

    /** Value of `api-version` in plugin.yml. */
    const val SPIGOT_API_VERSION = "1.21"

    /** Supported server range, used in the artifact name. Keep the README badge in step. */
    const val SUPPORTED_MINECRAFT_RANGE = "1.21-26.2"

    /**
     * Adventure. `adventure-platform-bukkit` is still built against the 4.x line, and
     * 5.x dropped types it links against (`net.kyori.adventure.audience.MessageType`
     * among them), so the whole Adventure stack has to stay on 4.x until the platform
     * modules release a 5.x-compatible version.
     */
    const val KYORI_PLATFORM_BUKKIT = "4.4.1"
    const val KYORI_ADVENTURE = "4.26.1"

    /**
     * Adventure major line `adventure-platform-bukkit` is built against, asserted by the
     * `checkAdventureAlignment` task. Raise it only once the platform modules ship a release
     * for the newer line — not to quiet the check after a dependency bump.
     */
    const val KYORI_ADVENTURE_MAJOR = "4"

    const val MULTIFICATION = "1.2.4"

    const val OKAERI_CONFIGS = "5.0.13"

    const val BSTATS_BUKKIT = "3.2.1"
    const val LITECOMMANDS = "3.11.0"

    const val JUNIT = "6.1.3"
    const val ASSERTJ = "3.27.7"
    const val MOCKITO = "5.23.0"
}
