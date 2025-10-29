import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile

plugins {
    id("cql.xsd-kotlin-multiplatform-gen-conventions")
    id("com.strumenta.antlr-kotlin") version "1.0.3"
}

kotlin {
    js {
        outputModuleName = "cql"
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "cql"
    }

    sourceSets {
        commonMain {
            kotlin {
                srcDir("build/generated/sources/antlr/commonMain/kotlin")
            }
            dependencies {
                api(project(":shared"))
                api("com.strumenta:antlr-kotlin-runtime:1.0.3")
            }
        }
        jvmMain {
            dependencies {
                api("com.strumenta:antlr-kotlin-runtime-jvm:1.0.3")
            }
        }
        jvmTest {
            dependencies {
                implementation(project(":quick"))
                implementation(project(":qdm"))
            }
        }
    }
}

val generateKotlinGrammarSource = tasks.register<AntlrKotlinTask>("generateKotlinGrammarSource") {
    dependsOn("cleanGenerateKotlinGrammarSource")
    source = fileTree("../../grammar") {
        include("**/*.g4")
    }
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

tasks.named("wasmJsSourcesJar") {
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

tasks.named("dokkaHtml") {
    dependsOn(generateKotlinGrammarSource)
}
