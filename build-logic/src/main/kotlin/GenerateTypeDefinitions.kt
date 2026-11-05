import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/** Generates TypeScript declaration files (.exports.d.mts) for the CQL NPM package. */
abstract class GenerateTypeDefinitions : DefaultTask() {

    /** Directory containing the compiled Kotlin/JS files. */
    @get:InputDirectory abstract val inputDir: DirectoryProperty

    /** The .d.mts file in the input directory with all type definitions. */
    @get:Input abstract val inputDMts: Property<String>

    /** Output directory for the generated .exports.d.mts files. */
    @get:OutputDirectory abstract val outputDir: DirectoryProperty

    /** Module names to generate type definitions for. */
    @get:Input abstract val modules: ListProperty<String>

    @TaskAction
    fun generate() {
        val inputDirFile = inputDir.get().asFile
        val outputDirFile = outputDir.get().asFile

        val inputDMts = inputDMts.get()
        val inputDMtsFile = File(inputDirFile, "$inputDMts.d.mts")
        val inputDMtsContent = inputDMtsFile.readText()

        val declaredTypes =
            Regex(
                    """^export declare (?:abstract )?(?:class|function|const|interface) (\w*)""",
                    RegexOption.MULTILINE,
                )
                .findAll(inputDMtsContent)
                .map { it.groupValues[1] }

        for (mod in modules.get()) {
            val mjsFile = File(inputDirFile, "$mod.mjs")
            val mjsFileContent = mjsFile.readText()

            val exports =
                Regex("""export \{([^}]+)};""")
                    .findAll(mjsFileContent)
                    .map { it.groupValues[1] }
                    .flatMap { Regex("""\w+ as (\w+)""").findAll(it) }
                    .map { it.groupValues[1] }
                    .filter { it in declaredTypes }

            val content = buildString {
                appendLine("export {")
                exports.forEach { appendLine("  $it,") }
                appendLine("} from \"./$inputDMts.d.mts\";")
            }

            File(outputDirFile, "$mod.exports.d.mts").writeText(content)
        }
    }
}
