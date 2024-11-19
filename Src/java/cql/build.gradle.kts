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
                implementation("com.strumenta:antlr-kotlin-runtime:1.0.1")
            }
        }

        jvmMain {
            dependencies {
                api("com.strumenta:antlr-kotlin-runtime-jvm:1.0.0")
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
}

tasks.withType<AbstractKotlinCompile<*>> {
    dependsOn(generateKotlinGrammarSource)
}