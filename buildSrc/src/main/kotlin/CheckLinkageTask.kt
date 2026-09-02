import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import java.io.File
import java.util.zip.ZipFile

/**
 * Verifies that compiled code links against a library it was not compiled against.
 *
 * This build has two places where that is deliberately the case, and it is not an oversight in
 * either: LiteCommands publishes no Minestom binding for the framework version the rest of the
 * plugin uses, and Adventure for Fabric trails the version this plugin pins because the release
 * that caught up is for a Minecraft the Fabric command binding cannot follow. Both are the least
 * bad option available, which is a different thing from being safe.
 *
 * So the build measures it. Every method and field reference the subject bytecode emits into the
 * named package is resolved against the classes that will actually be on the classpath, walking
 * supertypes as the JVM would. A member that has been dropped fails the build here rather than
 * throwing NoSuchMethodError at the first message somebody sends.
 */
abstract class CheckLinkageTask : DefaultTask() {

    /** Jars or class directories whose bytecode is examined. */
    @get:InputFiles
    @get:Classpath
    abstract val subjects: ConfigurableFileCollection

    /** What those classes will find on the classpath at runtime. */
    @get:InputFiles
    @get:Classpath
    abstract val provided: ConfigurableFileCollection

    /** Internal-name prefix of the references to check, e.g. `net/kyori/adventure/`. */
    @get:Input
    abstract val packagePrefix: Property<String>

    /**
     * Prefixes inside that one to leave alone.
     *
     * For the parts of a library whose own version is not the question. Adventure's platform
     * bridges are versioned with the platform rather than with Adventure, and are published
     * against a different set of mappings again, so their signatures say nothing about whether
     * the Adventure line underneath is the right one.
     */
    @get:Input
    abstract val ignoredPrefixes: SetProperty<String>

    /** Named in the report and in the failure, e.g. "Adventure 4.25.0". */
    @get:Input
    abstract val providedDescription: Property<String>

    /** Printed when the check fails, to say what the reader should do about it. */
    @get:Input
    abstract val advice: Property<String>

    @get:OutputFile
    abstract val report: RegularFileProperty

    private data class MemberReference(
        val owner: String,
        val name: String,
        val descriptor: String,
        val field: Boolean
    )

    private data class ApiClass(
        val superName: String?,
        val interfaces: List<String>,
        val methods: Set<String>,
        val fields: Set<String>
    )

    @TaskAction
    fun check() {

        val prefix = packagePrefix.get()

        val ownClasses = readClasses(subjects.files)
        val api = readClasses(provided.files) + ownClasses

        val ignored = ignoredPrefixes.get()
        val references = readReferences(prefix)
            .filterNot { reference -> ignored.any { reference.owner.startsWith(it) } }
            .toSet()
        val borrowed = references.map { it.owner }.filterNot { it in ownClasses }.toSortedSet()

        val problems = mutableListOf<String>()

        for (reference in references.sortedBy { "${it.owner}#${it.name}${it.descriptor}" }) {

            // Classes that travel with the subject answer for themselves.
            if (reference.owner in ownClasses) {
                continue
            }

            if (!api.containsKey(reference.owner)) {
                problems += "unknown type ${reference.owner}"
                continue
            }

            if (!hasMember(api, reference.owner, reference)) {
                problems += "unknown ${if (reference.field) "field" else "method"} " +
                        "${reference.owner}#${reference.name}${if (reference.field) "" else reference.descriptor}"
            }
        }

        val distinct = problems.distinct()

        val file = report.get().asFile
        file.parentFile.mkdirs()
        file.writeText(
            buildString {
                appendLine("resolved against ${providedDescription.get()}")
                appendLine("types borrowed: ${borrowed.size}, members referenced: ${references.size}")
                appendLine()
                if (distinct.isEmpty()) appendLine("OK") else distinct.forEach { appendLine(it) }
            }
        )

        if (distinct.isNotEmpty()) {
            throw GradleException(
                distinct.joinToString(
                    prefix = "Does not link against ${providedDescription.get()}:\n  - ",
                    separator = "\n  - ",
                    postfix = "\n\n${advice.get()}"
                )
            )
        }
    }

    private fun readClasses(files: Set<File>): Map<String, ApiClass> {

        val classes = mutableMapOf<String, ApiClass>()

        forEachClass(files) { bytes ->
            val reader = ClassReader(bytes)
            val methods = mutableSetOf<String>()
            val fields = mutableSetOf<String>()

            reader.accept(object : ClassVisitor(Opcodes.ASM9) {

                override fun visitMethod(
                    access: Int, name: String, descriptor: String,
                    signature: String?, exceptions: Array<out String>?
                ): MethodVisitor? {
                    methods += "$name$descriptor"
                    return null
                }

                override fun visitField(
                    access: Int, name: String, descriptor: String,
                    signature: String?, value: Any?
                ): FieldVisitor? {
                    fields += name
                    return null
                }
            }, ClassReader.SKIP_CODE or ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)

            classes[reader.className] = ApiClass(
                superName = reader.superName,
                interfaces = reader.interfaces.toList(),
                methods = methods,
                fields = fields
            )
        }

        return classes
    }

    private fun readReferences(prefix: String): Set<MemberReference> {

        val references = mutableSetOf<MemberReference>()

        forEachClass(subjects.files) { bytes ->
            ClassReader(bytes).accept(object : ClassVisitor(Opcodes.ASM9) {

                override fun visitMethod(
                    access: Int, name: String, descriptor: String,
                    signature: String?, exceptions: Array<out String>?
                ): MethodVisitor = object : MethodVisitor(Opcodes.ASM9) {

                    override fun visitFieldInsn(
                        opcode: Int, owner: String, name: String, descriptor: String
                    ) {
                        if (owner.startsWith(prefix)) {
                            references += MemberReference(owner, name, descriptor, field = true)
                        }
                    }

                    override fun visitMethodInsn(
                        opcode: Int, owner: String, name: String,
                        descriptor: String, isInterface: Boolean
                    ) {
                        if (owner.startsWith(prefix)) {
                            references += MemberReference(owner, name, descriptor, field = false)
                        }
                    }
                }
            }, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
        }

        return references
    }

    private fun forEachClass(files: Set<File>, action: (ByteArray) -> Unit) {

        files.forEach { file ->
            when {
                file.isDirectory ->
                    file.walkTopDown()
                        .filter { it.isFile && it.name.endsWith(".class") }
                        .forEach { action(it.readBytes()) }

                file.isFile && file.name.endsWith(".jar") ->
                    ZipFile(file).use { zip ->
                        zip.entries().asSequence()
                            .filter { it.name.endsWith(".class") }
                            .forEach { entry -> action(zip.getInputStream(entry).use { it.readBytes() }) }
                    }
            }
        }
    }

    private fun hasMember(
        api: Map<String, ApiClass>,
        owner: String,
        reference: MemberReference
    ): Boolean {

        val current = api[owner] ?: return hasJdkMember(owner, reference)

        if (reference.field && reference.name in current.fields) return true
        if (!reference.field && "${reference.name}${reference.descriptor}" in current.methods) return true

        // Constructors are never inherited; anything else may come from a supertype.
        if (reference.name == "<init>") return false

        val parents = buildList {
            current.superName?.let { add(it) }
            addAll(current.interfaces)
        }

        return parents.any { hasMember(api, it, reference) }
    }

    /**
     * Library types inherit from the JDK - `java.lang.Object`, and `java.lang.Enum` for every enum
     * constant a configuration names. Those supertypes are not in the jars being checked, so
     * resolve them against the running JVM rather than reporting them missing.
     */
    private fun hasJdkMember(owner: String, reference: MemberReference): Boolean {

        if (!owner.startsWith("java/")) return false

        val type = runCatching { Class.forName(owner.replace('/', '.')) }.getOrNull() ?: return false

        if (reference.field) {
            return generateSequence(type) { it.superclass }
                .any { candidate -> candidate.declaredFields.any { it.name == reference.name } }
        }

        return generateSequence(type) { it.superclass }
            .any { candidate ->
                candidate.declaredMethods.any {
                    it.name == reference.name && Type.getMethodDescriptor(it) == reference.descriptor
                }
            }
    }
}
