import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile

plugins {
    id("cql.xsd-kotlin-multiplatform-gen-conventions")
    id("com.strumenta.antlr-kotlin") version "1.0.1"
}

kotlin {
    sourceSets {
        commonMain {
            kotlin {
                srcDir("build/generated/sources/antlr/commonMain/kotlin")
                srcDir("build/generated/sources/cql/commonMain/kotlin")
            }
            dependencies {
                api("com.strumenta:antlr-kotlin-runtime:1.0.1")
                api("org.jetbrains.kotlinx:kotlinx-io-core:0.6.0")
            }
        }

        jvmTest {
            dependencies {
                implementation(project(":serialization"))
            }
        }
    }
}

val generateKotlinGrammarSource = tasks.register<AntlrKotlinTask>("generateKotlinGrammarSource") {
    dependsOn("cleanGenerateKotlinGrammarSource")
    source = fileTree("../../grammar")
    packageName = "org.cqframework.cql.gen"
    arguments = listOf("-visitor")
    outputDirectory = file("build/generated/sources/antlr/commonMain/kotlin/${packageName!!.replace(".", "/")}")
    outputs.dirs(outputDirectory)
}

tasks.named("jvmSourcesJar") {
    dependsOn(generateKotlinGrammarSource)
}

tasks.named("jsSourcesJar") {
    dependsOn(generateKotlinGrammarSource)
}

tasks.named("sourcesJar") {
    dependsOn(generateKotlinGrammarSource)
}

tasks.withType<AbstractKotlinCompile<*>> {
    dependsOn(generateKotlinGrammarSource)
}

tasks.named("dokkaJavadocJar") {
    dependsOn(generateKotlinGrammarSource)
}