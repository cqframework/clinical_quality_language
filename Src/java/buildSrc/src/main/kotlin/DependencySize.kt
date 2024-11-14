import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvedDependency

class DependencySize : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("dependencySizeReport") {
            group = "reporting"
            description = "Displays the size of all dependencies, ordered from largest to smallest"

            doLast {
                val configuration = project.configurations.getByName("runtimeClasspath")
                val resolvedArtifacts = configuration.resolvedConfiguration.firstLevelModuleDependencies

                val dependencySizes = mutableMapOf<String, Long>()

                resolvedArtifacts.forEach { dep ->
                    collectDependencySizes(dep, dependencySizes, mutableSetOf())
                }

                val sizes = dependencySizes.entries.sortedByDescending { it.value }

                println("Dependency Size Report (Largest to Smallest):")
                sizes.forEach { (name, size) ->
                    println("${name.padEnd(50)} ${size / (1024 * 1024)} MB")
                }
            }
        }
    }

    private fun collectDependencySizes(
        dependency: ResolvedDependency,
        dependencySizes: MutableMap<String, Long>,
        visited: MutableSet<String>
    ) {
        if (visited.contains(dependency.module.id.toString())) return

        visited.add(dependency.module.id.toString())

        val dependencyArtifacts = dependency.moduleArtifacts
        dependencyArtifacts.forEach { artifact ->
            val file = artifact.file
            if (file.exists() && file.isFile) {
                dependencySizes[file.name] = file.length()
            }
        }

        dependency.children.forEach { childDep ->
            collectDependencySizes(childDep, dependencySizes, visited)
        }
    }
}
