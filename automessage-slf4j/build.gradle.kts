// One SLF4J adapter for the three platforms handed an SLF4J logger - Velocity injects one,
// Minestom builds on one, Fabric inherits Minecraft's. Sponge is deliberately not among them: it
// hands out a Log4j logger, which is a different interface.
//
// compileOnly, because all three already provide SLF4J at runtime; a shaded second copy would be
// a logger nobody is reading.
dependencies {
    api(project(":automessage-api"))

    compileOnly("org.slf4j:slf4j-api:${Versions.SLF4J}")
}
