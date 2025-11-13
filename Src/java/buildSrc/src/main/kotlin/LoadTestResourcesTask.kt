import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*

/**
 * Generates multiplatform TestResource classes that provide access to test resources from
 * src/commonTest/resources.
 */
open class LoadTestResourcesTask : DefaultTask() {

    @get:InputDirectory val inputDir = File(project.projectDir, "src/commonTest/resources")

    @get:OutputDirectory
    val outputDir = File(project.projectDir, "build/generated/sources/testResources")

    fun getDirForSourceSet(sourceSetName: String): File {
        return File(outputDir, sourceSetName)
    }

    private fun getKotlinDirForSourceSet(sourceSetName: String): File {
        return File(getDirForSourceSet(sourceSetName), "kotlin")
    }

    @TaskAction
    fun generate() {
        val packageName = "org.cqframework.cql.shared"
        val fileName = "TestResource"

        // commonTest/kotlin/TestResource.kt
        FileSpec.builder(packageName, fileName)
            .addType(
                TypeSpec.classBuilder("TestResource")
                    .addModifiers(KModifier.EXPECT)
                    .primaryConstructor(
                        FunSpec.constructorBuilder().addParameter("path", String::class).build()
                    )
                    .addFunction(FunSpec.builder("readText").returns(String::class).build())
                    .build()
            )
            .build()
            .writeTo(getKotlinDirForSourceSet("commonTest"))

        // jvmTest/kotlin/TestResourcesJvm.kt
        FileSpec.builder(packageName, "${fileName}Jvm")
            .addType(
                TypeSpec.classBuilder("TestResource")
                    .addModifiers(KModifier.ACTUAL)
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addModifiers(KModifier.ACTUAL)
                            .addParameter("path", String::class)
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder("path", String::class).initializer("path").build()
                    )
                    .addFunction(
                        FunSpec.builder("readText")
                            .addModifiers(KModifier.ACTUAL)
                            .returns(String::class)
                            .addCode(
                                """
                                return this::class.java.classLoader.getResourceAsStream(path)?.bufferedReader()?.use { it.readText() }
                                    ?: error("TestResource not found: ${"$"}path")
                                """
                                    .trimIndent()
                            )
                            .build()
                    )
                    .build()
            )
            .build()
            .writeTo(getKotlinDirForSourceSet("jvmTest"))

        val relativePathToFileContentMap =
            inputDir
                .walkTopDown()
                .filter { it.isFile }
                .associateBy(
                    { inputDir.toPath().relativize(it.toPath()).toString().replace("\\", "/") },
                    { Files.readString(it.toPath(), StandardCharsets.UTF_8) },
                )

        fun FileSpec.Builder.addTestResourceClassWithPreloadedResources(): FileSpec.Builder {
            addProperty(
                PropertySpec.builder(
                        "preloadedResources",
                        Map::class.parameterizedBy(String::class, String::class),
                        KModifier.PRIVATE,
                    )
                    .initializer(
                        CodeBlock.builder()
                            .apply {
                                add("mapOf(\n")
                                for ((relPath, fileContent) in relativePathToFileContentMap) {
                                    add("  %S to %S,\n", relPath, fileContent)
                                }
                                add(")")
                            }
                            .build()
                    )
                    .build()
            )

            addType(
                TypeSpec.classBuilder("TestResource")
                    .addModifiers(KModifier.ACTUAL)
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addModifiers(KModifier.ACTUAL)
                            .addParameter("path", String::class)
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder("path", String::class).initializer("path").build()
                    )
                    .addFunction(
                        FunSpec.builder("readText")
                            .addModifiers(KModifier.ACTUAL)
                            .returns(String::class)
                            .addCode(
                                """
                                return preloadedResources[path] ?: error("TestResource not found: ${"$"}path")
                                """
                                    .trimIndent()
                            )
                            .build()
                    )
                    .build()
            )

            return this
        }

        // jsTest/kotlin/TestResourcesJs.kt
        FileSpec.builder(packageName, "${fileName}Js")
            .addTestResourceClassWithPreloadedResources()
            .build()
            .writeTo(getKotlinDirForSourceSet("jsTest"))

        // wasmJsTest/kotlin/TestResourcesWasmJs.kt
        FileSpec.builder(packageName, "${fileName}WasmJs")
            .addTestResourceClassWithPreloadedResources()
            .build()
            .writeTo(getKotlinDirForSourceSet("wasmJsTest"))
    }
}
