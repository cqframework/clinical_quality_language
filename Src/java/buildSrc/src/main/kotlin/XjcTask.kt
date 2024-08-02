import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import java.io.File

open class XjcTask : DefaultTask() {
    @Input
    lateinit var schemaDir: String

    @OutputDirectory
    lateinit var outputDir: String

    @Input
    var extraArgs : List<String> = emptyList()

    @TaskAction
    fun generate() {
        val defaultArgs = listOf("-disableXmlSecurity", "-Xfluent-api", "-Xequals" ,"-XhashCode", "-XtoString" , "-Xsetters", "-Xsetters-mode=direct")
        val options = listOf("-d", outputDir, schemaDir) + defaultArgs + extraArgs

        project.javaexec {
            mainClass.set("com.sun.tools.xjc.XJCFacade")
            classpath = project.configurations.getByName("xjc")
            args = options
        }
    }
}