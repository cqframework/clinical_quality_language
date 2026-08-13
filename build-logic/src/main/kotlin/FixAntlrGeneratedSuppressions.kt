import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Removes ANTLR-generated `@Suppress("UNSAFE_CALL")` which triggers compiler warnings about
 * suppressing errors.
 */
abstract class FixAntlrGeneratedSuppressions : DefaultTask() {

    @get:InputDirectory abstract val inputDir: DirectoryProperty

    @get:OutputDirectory abstract val outputDir: DirectoryProperty

    @TaskAction
    fun run() {
        project.sync {
            from(inputDir)
            into(outputDir)
        }

        val suppress = "@file:Suppress(\"UNNECESSARY_SAFE_CALL\")"
        outputDir
            .get()
            .asFile
            .walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .forEach { f ->
                var text = f.readText()
                text = text.replace("    @Suppress(\"UNSAFE_CALL\")\n", "")
                if (!text.startsWith("@file:Suppress")) {
                    text = "$suppress\n\n$text"
                }
                f.writeText(text)
            }
    }
}
