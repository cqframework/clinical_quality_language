import com.squareup.kotlinpoet.*
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class FilesToStringsTask : DefaultTask() {
    /** Maps input files to variable names in the generated file */
    @get:Input abstract var inputFiles: Map<File, String>

    @get:OutputDirectory abstract var outputDir: File

    @get:Input abstract var packageName: String

    @get:Input abstract var fileName: String

    @TaskAction
    fun generate() {
        FileSpec.builder(packageName, fileName)
            .apply {
                for ((fileProp, varName) in inputFiles) {
                    val fileContent = Files.readString(fileProp.toPath(), StandardCharsets.UTF_8)
                    addProperty(
                        PropertySpec.builder(varName, String::class)
                            .initializer("%S", fileContent)
                            .build()
                    )
                }
            }
            .build()
            .writeTo(outputDir)
    }
}
