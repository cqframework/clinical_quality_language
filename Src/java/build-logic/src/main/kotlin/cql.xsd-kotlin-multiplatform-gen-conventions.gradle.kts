import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile

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

tasks.withType<XsdKotlinGenTask>().configureEach {
    mustRunAfter(tasks.withType<Delete>())
}

val suppressGeneratedWarnings = tasks.register("suppressGeneratedWarnings") {
    description = "Add @file:Suppress annotations to generated Kotlin files"
    dependsOn(xsdKotlinGenTask)
    mustRunAfter(xsdKotlinGenTask)
    doLast {
        val generatedDir = layout.buildDirectory.dir("generated/sources").get().asFile
        if (generatedDir.exists()) {
            val suppress = "@file:Suppress(\"UNNECESSARY_SAFE_CALL\")"
            fileTree(generatedDir) { include("**/*.kt") }.files.forEach { f ->
                var text = f.readText()
                // Remove ANTLR-generated @Suppress("UNSAFE_CALL") which triggers
                // compiler warnings about suppressing errors
                text = text.replace("    @Suppress(\"UNSAFE_CALL\")\n", "")
                if (!text.startsWith("@file:Suppress")) {
                    text = "$suppress\n\n$text"
                }
                f.writeText(text)
            }
        }
    }
}

tasks.withType<AbstractKotlinCompile<*>>().configureEach {
    dependsOn(xsdKotlinGenTask)
    dependsOn(suppressGeneratedWarnings)
}

tasks.named("clean").configure {
    doLast {
        delete(destDir)
    }
}