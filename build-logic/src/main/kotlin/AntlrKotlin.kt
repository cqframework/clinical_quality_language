import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask
import java.io.File
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.TaskProvider

/**
 * Registers a task to generate Kotlin sources from ANTLR grammar files. The generated sources have
 * the `@Suppress("UNSAFE_CALL")` annotations fixed to avoid compiler warnings about suppressing
 * errors.
 */
fun Project.registerAntlrKotlinGrammarGeneration(
    taskName: String,
    grammarDir: FileTree,
    outputDir: File,
    packageName: String,
    arguments: List<String> = listOf("-visitor"),
): TaskProvider<AntlrKotlinTask> {
    return tasks.register(taskName, AntlrKotlinTask::class.java) {
        source = grammarDir.matching { include("**/*.g4") }
        this.packageName = packageName
        this.arguments = arguments
        outputDirectory = outputDir

        doLast {
            val suppress = "@file:Suppress(\"UNNECESSARY_SAFE_CALL\")"
            outputDirectory!!
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
}
