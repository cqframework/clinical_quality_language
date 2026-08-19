import com.squareup.kotlinpoet.*
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class FileToString : DefaultTask() {
    @get:InputFile abstract val inputFile: RegularFileProperty

    @get:OutputDirectory abstract val outputDir: DirectoryProperty

    @get:Input abstract val packageName: Property<String>

    @get:Input abstract val variableName: Property<String>

    @TaskAction
    fun generate() {
        val fileContent = inputFile.get().asFile.readText()
        val variableName = variableName.get()

        FileSpec.builder(packageName.get(), variableName.replaceFirstChar { it.uppercase() })
            .addProperty(
                PropertySpec.builder(variableName, String::class)
                    .initializer("%S", fileContent)
                    .build()
            )
            .build()
            .writeTo(outputDir.get().asFile)
    }
}
