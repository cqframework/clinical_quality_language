import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("com.strumenta.antlr-kotlin") version "1.0.0-RC4"
    id("org.jetbrains.kotlin.multiplatform")
}

repositories {
    mavenCentral()
}

kotlin {
//    applyDefaultHierarchyTemplate()
    jvm()
    jvmToolchain(11)
    js()
    js {
        browser()
        binaries.executable()
    }


    sourceSets {
        val commonMain by getting {
            kotlin {
                srcDir(layout.buildDirectory.dir("generated/sources/antlr/kotlin"))
            }

            dependencies {
                implementation("com.strumenta:antlr-kotlin-runtime:1.0.0-RC4")
            }
        }
    }
}

val generateKotlinGrammarSource = tasks.register<AntlrKotlinTask>("generateKotlinGrammarSource") {
    dependsOn("cleanGenerateKotlinGrammarSource")

    // ANTLR .g4 files are under {example-project}/antlr
    // Only include *.g4 files. This allows tools (e.g., IDE plugins)
    // to generate temporary files inside the base path
    source = fileTree(layout.projectDirectory.dir("../../grammar")) {
        include("**/*.g4")
    }

    // We want the generated source files to have this package name
    val pkgName = "org.cqframework.cql.gen"
    packageName = pkgName

    // We want visitors alongside listeners.
    // The Kotlin target language is implicit, as is the file encoding (UTF-8)
    arguments = listOf("-visitor")

    val outDir = "generated/sources/antlr/kotlin/main/${pkgName.replace(".", "/")}"
    outputDirectory = layout.buildDirectory.dir(outDir).get().asFile
}

tasks.withType<KotlinCompilationTask<*>> {
    dependsOn(generateKotlinGrammarSource)
}

tasks.withType<JavaCompile> {
    dependsOn(tasks.withType<KotlinCompilationTask<*>>())
}

