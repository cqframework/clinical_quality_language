import org.jetbrains.kotlin.gradle.tasks.KotlinCompileTool
import com.github.gradle.node.task.NodeTask

plugins {
    id("cql.kotlin-multiplatform-conventions")
    id("com.github.node-gradle.node")
}

val buildDir = project.layout.buildDirectory.get().toString()
val destDir = "${buildDir}/generated/sources/$name/commonMain/kotlin"

kotlin {
    sourceSets {
        commonMain {
            kotlin {
                srcDir(destDir)
            }
        }
    }
}

node {
    download = true
    nodeProjectDir.set(file("../../js/xsd-kotlin-gen"))
}

val runXsdKotlinGenTask = tasks.register<NodeTask>("runXsdKotlinGen") {
    dependsOn(tasks.npmInstall)
    script.set(file("../../js/xsd-kotlin-gen/generate.js"))
    args.set(listOf("--project=${project.name}"))
}

tasks.withType<KotlinCompileTool>().configureEach {
    dependsOn(runXsdKotlinGenTask)
}

tasks.withType<JavaCompile>().configureEach {
    dependsOn(runXsdKotlinGenTask)
}

tasks.withType<Delete>().configureEach {
    delete(destDir)
}