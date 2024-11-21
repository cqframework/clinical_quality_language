import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.process.ExecOperations;

import javax.inject.Inject;

open class XjcTask @Inject constructor(
    private val execOperations: ExecOperations
)  : DefaultTask() {
    @Input
    lateinit var schema: String

    @Input
    var extraArgs : List<String> = emptyList()

    @Input
    var binding: String = ""

    @OutputDirectory
    lateinit var outputDir: String

    @TaskAction
    fun generate() {
        var bindingArgs : List<String> = emptyList();
        if (binding.isNotBlank()) {
            bindingArgs = listOf("-b", binding)
        }

        val defaultArgs = listOf("-quiet", "-disableXmlSecurity", "-Xfluent-api", "-Xequals" ,"-XhashCode", "-XtoString" , "-Xsetters", "-Xsetters-mode=direct")
        val options = listOf("-d", outputDir, schema) + bindingArgs + defaultArgs + extraArgs

        execOperations.javaexec {
            mainClass.set("com.sun.tools.xjc.XJCFacade")
            classpath = project.configurations.getByName("xjc")
            args = options
        }
    }
}