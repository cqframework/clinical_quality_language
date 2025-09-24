import org.jetbrains.kotlin.gradle.tasks.KotlinCompileTool

plugins {
    id("cql.kotlin-multiplatform-conventions")
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

val xsdKotlinGenTask = tasks.register<XsdKotlinGenTask>("runXsdKotlinGen")

tasks.withType<KotlinCompileTool>().configureEach {
    dependsOn(xsdKotlinGenTask)
}

tasks.withType<JavaCompile>().configureEach {
    dependsOn(xsdKotlinGenTask)
}

tasks.withType<Delete>().configureEach {
    delete(destDir)
}