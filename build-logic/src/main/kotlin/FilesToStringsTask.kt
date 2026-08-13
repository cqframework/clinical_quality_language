import com.squareup.kotlinpoet.*
import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class FilesToStringsTask : DefaultTask() {
    /** Maps input files to variable names in the generated file */
    @get:Input abstract var inputFiles: Map<File, String>

    @get:OutputDirectory abstract val outputDir: DirectoryProperty

    @get:Input abstract val packageName: Property<String>

    @get:Input abstract val fileName: Property<String>

    @TaskAction
    fun generate() {
        FileSpec.builder(packageName.get(), fileName.get())
            .apply {
                for ((fileProp, varName) in inputFiles) {
                    val fileContent = fileProp.readText()
                    addProperty(
                        PropertySpec.builder(varName, String::class)
                            .initializer("%S", fileContent)
                            .build()
                    )
                }
            }
            .build()
            .writeTo(outputDir.get().asFile)
    }
}
