import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.gradle.node.task.NodeTask
import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.node-gradle.node")
}

dependencies {
    implementation("io.github.pdvrieze.xmlutil:core:0.90.3")
    implementation("io.github.pdvrieze.xmlutil:serialization:0.90.3")
    implementation("io.github.pdvrieze.xmlutil:serialization-jvm:0.90.3")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
}

node {
    download = true
    nodeProjectDir.set(file("../../js/xsd-kotlin-gen"))
}

val buildDir = project.layout.buildDirectory.get().toString()
val destDir = "${buildDir}/generated/sources/$name/main/java"

val runXsdKotlinGenTask = tasks.register<NodeTask>("runXsdKotlinGen") {
    dependsOn(tasks.npmInstall)
    script.set(file("../../js/xsd-kotlin-gen/generate.js"))
}

tasks.withType<KotlinCompile>().configureEach {
    dependsOn(runXsdKotlinGenTask)
}

tasks.withType<JavaCompile>().configureEach {
    dependsOn(runXsdKotlinGenTask)
}

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