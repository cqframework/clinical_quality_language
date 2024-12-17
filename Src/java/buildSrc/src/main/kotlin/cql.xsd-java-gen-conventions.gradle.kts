import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.gradle.node.task.NodeTask
import com.github.gradle.node.npm.task.NpmInstallTask

plugins {
    id("cql.library-conventions")
    id("com.github.node-gradle.node")
}

dependencies {
    api("jakarta.xml.bind:jakarta.xml.bind-api:4.0.1")
    api("codes.rafael.jaxb2_commons:jaxb2-basics-runtime:3.0.0")
}

node {
    download = true
    nodeProjectDir = file("../../js/xsd-java-gen")
}

val buildDir = project.layout.buildDirectory.get().toString()
val destDir = "${buildDir}/generated/sources/$name/main/java"

tasks.register<NodeTask>("runXsdJavaGen") {
    dependsOn(tasks.withType<NpmInstallTask>())
    script = file("../../js/xsd-java-gen/generate.js")
}

tasks.withType<KotlinCompile>().configureEach {
    dependsOn(tasks.withType<NodeTask>())
}

tasks.withType<JavaCompile>().configureEach {
    dependsOn(tasks.withType<NodeTask>())
}

//tasks.withType<NodeTask>().configureEach {
//    outputDir = destDir
//    outputs.dir(outputDir)
//}

tasks.withType<Delete>().configureEach {
    delete(destDir)
}

sourceSets {
    main {
        java {
            srcDir(destDir)
        }
    }
}
