import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.gradle.node.task.NodeTask
import gradle.kotlin.dsl.accessors._2dcd0a84416f85634c85a37519569374.sourceSets
import org.gradle.kotlin.dsl.kotlin

plugins {
    id("cql.kotlin-multiplatform-conventions")
    kotlin("plugin.serialization")
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
            dependencies {
                implementation("io.github.pdvrieze.xmlutil:core:0.90.3")
                implementation("io.github.pdvrieze.xmlutil:serialization:0.90.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
            }
        }

        jvmMain {
            dependencies {
                implementation("io.github.pdvrieze.xmlutil:serialization-jvm:0.90.3")
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