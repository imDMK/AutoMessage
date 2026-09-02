object Versions {

    /** JDK the project is built with. */
    const val JAVA_TOOLCHAIN = 25

    /**
     * Bytecode level of the shipped plugin, and therefore the oldest JVM a server may
     * run it on. Kept at 21 because that is what Minecraft 1.21 itself requires — a
     * higher target would lock the plugin out of the low end of the supported range.
     */
    const val JAVA_RELEASE = 21

    /** Spigot API the plugin is compiled against (newest supported server). */
    const val SPIGOT_API = "26.2-R0.1-SNAPSHOT"

    /**
     * Folia's fork of the Bukkit API, which adds the regionised schedulers.
     *
     * Deliberately not the newest. Folia 26.2 requires Java 25, and building against it would
     * force this module's bytecode above the level Folia servers on 1.21 can load. The scheduler
     * interfaces have not changed since, so the older API produces a jar that runs on both.
     */
    /** Folia build the runFolia task downloads; Folia publishes releases per Minecraft line. */
    const val FOLIA_RUN_MINECRAFT = "26.2"

    const val FOLIA_API = "1.21.11-R0.1-SNAPSHOT"

    /**
     * The Velocity proxy API.
     *
     * Deliberately the 3.x line rather than 4.x: it builds against Adventure 4.26.1, the exact
     * version this plugin pins, so the proxy and the plugin agree on what a Component is. Velocity
     * 4 moves to Adventure 5, which is the incompatibility the `checkAdventureAlignment` task
     * exists to catch.
     */
    const val VELOCITY_API = "3.5.1"

    /**
     * Minestom.
     *
     * Deliberately not the newest, for the same reason as Velocity: this is the last release built
     * against Adventure 4.26.1. Everything after it moved to Adventure 5, which the whole project
     * cannot follow while adventure-platform-bukkit still links against types 5.x removed.
     */
    const val MINESTOM = "2026.05.11-1.21.11"

    /** Minecraft this Minestom build speaks; it targets exactly one, not a range. */
    const val MINESTOM_MINECRAFT = "1.21.11"

    /**
     * Bytecode level of the Minestom jar, and the one case where raising it is right.
     *
     * Minestom itself is compiled to Java 25 - every release in the Adventure 4.x window is, so
     * there is no older build to retreat to and no Minestom server running on anything less. The
     * mirror image of Folia, where raising the level would have locked the plugin out of servers
     * that exist; here it locks out nobody, and leaving it at 21 would mean Gradle refusing to
     * resolve Minestom at all.
     */
    const val MINESTOM_JAVA_RELEASE = 25

    /**
     * Minecraft the Fabric mod is built for.
     *
     * The last version Fabric publishes intermediary and Yarn mappings for. Everything after it
     * moved to a mapping scheme LiteCommands' Fabric binding does not speak - it is compiled
     * against intermediary names (`net.minecraft.class_2168`), which 26.x no longer has.
     */
    const val FABRIC_MINECRAFT = "1.21.11"

    const val FABRIC_YARN = "1.21.11+build.6"
    const val FABRIC_LOADER = "0.19.5"
    const val FABRIC_API = "0.141.6+1.21.11"

    /**
     * Adventure for Fabric, which the mod loader provides rather than the plugin shading.
     *
     * Deliberately 6.8.0 rather than 6.9.0. The newer one is built against Adventure 4.26.1 -
     * the version this plugin pins - but for Minecraft 26.1.2, which is past where the Fabric
     * command binding can follow. So this one trails at Adventure 4.25.0, and
     * `checkFabricAdventureLinkage` is what makes that a measured choice rather than a hope:
     * it resolves every Adventure member the plugin calls against the 4.25.0 the mod will
     * actually load.
     */
    const val ADVENTURE_PLATFORM_FABRIC = "6.8.0"

    /**
     * Adventure the Fabric mod will actually load, bundled inside adventure-platform-fabric.
     *
     * One minor line behind what the rest of the plugin compiles against, which is the price of
     * the pin above. `checkAdventureLinkage` in the Fabric module resolves everything the shipped
     * bytecode calls against exactly these jars.
     */
    const val ADVENTURE_FABRIC = "4.25.0"

    /**
     * The Sponge API.
     *
     * The newest stable line, and one that builds against Adventure 4.26.1 - the same version this
     * plugin pins, so a Component built here is one Sponge can render.
     */
    const val SPONGE_API = "17.0.0"

    /** Minecraft that SpongeAPI 17 is the API for; SpongeVanilla publishes the pairing. */
    const val SPONGE_MINECRAFT = "1.21.10"

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

    /** Soft dependency: only touched when the server actually has PlaceholderAPI installed. */
    const val PLACEHOLDER_API = "2.12.3"

    const val BSTATS_BUKKIT = "3.2.1"

    const val BSTATS_VELOCITY = "3.2.1"

    const val BSTATS_SPONGE = "3.2.1"
    const val LITECOMMANDS = "3.11.0"

    /** Last version with a Minestom binding; the Minestom module pins the whole stack to it. */
    const val LITECOMMANDS_MINESTOM = "3.10.9"


    /** Provided by Velocity, Minestom and Fabric alike; never shaded. */
    const val SLF4J = "2.0.17"

    const val JUNIT = "6.1.3"
    const val ASSERTJ = "3.27.7"
    const val MOCKITO = "5.23.0"
}
