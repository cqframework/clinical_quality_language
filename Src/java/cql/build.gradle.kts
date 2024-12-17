import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile

plugins {
    id("cql.kotlin-multiplatform-conventions")
    id("com.strumenta.antlr-kotlin") version "1.0.1"
}

kotlin {
    sourceSets {
        commonMain {
            kotlin {
                srcDir("build/generated/sources/antlr/main/kotlin")
            }
            dependencies {
                api("com.strumenta:antlr-kotlin-runtime:1.0.1")
            }
        }
    }
}

val generateKotlinGrammarSource = tasks.register<AntlrKotlinTask>("generateKotlinGrammarSource") {
    dependsOn("cleanGenerateKotlinGrammarSource")
    source = fileTree("../../grammar")
    packageName = "org.cqframework.cql.gen"
    arguments = listOf("-visitor")
    outputDirectory = file("build/generated/sources/antlr/main/kotlin/${packageName!!.replace(".", "/")}")
    outputs.dirs(outputDirectory)
}

tasks.withType<AbstractKotlinCompile<*>> {
    dependsOn(generateKotlinGrammarSource)
}

tasks.named("dokkaJavadocJar") {
    dependsOn(generateKotlinGrammarSource)
}