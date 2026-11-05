import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.process.ExecOperations

abstract class XjcTask @Inject constructor(private val execOperations: ExecOperations) :
    DefaultTask() {
    @get:Input abstract val schema: Property<String>

    @get:Input abstract val extraArgs: ListProperty<String>

    @get:Input @get:Optional abstract val binding: Property<String>

    @get:OutputDirectory abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        var bindingArgs: List<String> = emptyList()
        val binding = binding.getOrElse("")
        if (binding.isNotBlank()) {
            bindingArgs = listOf("-b", binding)
        }

        val defaultArgs =
            listOf(
                "-quiet",
                "-disableXmlSecurity",
                "-Xfluent-api",
                "-Xequals",
                "-XhashCode",
                "-XtoString",
                "-Xsetters",
                "-Xsetters-mode=direct",
            )
        val options =
            listOf("-d", outputDir.get().asFile.absolutePath, schema.get()) +
                bindingArgs +
                defaultArgs +
                extraArgs.get()

        execOperations.javaexec {
            mainClass.set("com.sun.tools.xjc.XJCFacade")
            classpath = project.configurations.getByName("xjc")
            args = options
        }
    }
}
