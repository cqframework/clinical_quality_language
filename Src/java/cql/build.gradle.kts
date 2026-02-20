import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile

buildscript {
    dependencies {
        classpath("org.slf4j:slf4j-simple:1.7.36")
    }
}

plugins {
    id("cql.xsd-kotlin-multiplatform-gen-conventions")
    id("com.strumenta.antlr-kotlin") version "1.0.9"
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

val inlineModelInfoXmlsTask = tasks.register<FilesToStringsTask>("inlineModelInfoXmls") {
    inputFiles = mapOf(
        file("src/commonMain/resources/org/hl7/elm/r1/system-modelinfo.xml") to "systemModelInfoXml"
    )
    outputDir = file("build/generated/sources/inlineModelInfoXmls/commonMain/kotlin")
    packageName = "org.hl7.cql.model"
    fileName = "ModelInfoXmls.kt"
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
                srcDir(generateKotlinGrammarSource)
                srcDir(inlineModelInfoXmlsTask)
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

tasks.named("jvmSourcesJar") {
    dependsOn(generateKotlinGrammarSource)
    dependsOn(inlineModelInfoXmlsTask)
}

tasks.named("jsSourcesJar") {
    dependsOn(generateKotlinGrammarSource)
    dependsOn(inlineModelInfoXmlsTask)
}

tasks.named("wasmJsSourcesJar") {
    dependsOn(generateKotlinGrammarSource)
    dependsOn(inlineModelInfoXmlsTask)
}

tasks.named("sourcesJar") {
    dependsOn(generateKotlinGrammarSource)
    dependsOn(inlineModelInfoXmlsTask)
}

tasks.withType<AbstractKotlinCompile<*>> {
    dependsOn(generateKotlinGrammarSource)
    dependsOn(inlineModelInfoXmlsTask)
}

tasks.named("dokkaGeneratePublicationHtml") {
    dependsOn(generateKotlinGrammarSource)
    dependsOn(inlineModelInfoXmlsTask)
}
