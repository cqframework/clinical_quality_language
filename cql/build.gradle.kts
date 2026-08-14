import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

buildscript { dependencies { classpath("org.slf4j:slf4j-simple:1.7.36") } }

plugins { id("cql.kotlin-multiplatform-conventions") }

val generatedSourcesDir = project.layout.buildDirectory.dir("generated/sources")

val generateModelInfoKotlinSource by
    tasks.registering(XsdKotlinGenTask::class) {
        description = "Generates Kotlin sources for ModelInfo classes."
        inputXsd.set(rootProject.layout.projectDirectory.file("schemas/model/modelinfo.xsd"))
        outputDir.set(generatedSourcesDir.map { it.dir("cql") })
        jsExport.set(false)
    }

val generateKotlinGrammarSource =
    registerAntlrKotlinGrammarGeneration(
        taskName = "generateKotlinGrammarSource",
        grammarDir = rootProject.layout.projectDirectory.dir("grammar").asFileTree,
        packageName = "org.cqframework.cql.gen",
        outputDir = generatedSourcesDir.map { it.dir("antlr") }.get().asFile,
    )

val inlineSystemModelInfo by
    tasks.registering(FileToString::class) {
        inputFile.set(
            project.layout.projectDirectory.file(
                "src/commonMain/resources/org/hl7/elm/r1/system-modelinfo.xml"
            )
        )
        outputDir.set(generatedSourcesDir.map { it.dir("inlineSystemModelInfo") })
        packageName.set("org.hl7.cql.model")
        variableName.set("systemModelInfoXml")
    }

kotlin {
    js { outputModuleName = "cql" }

    @OptIn(ExperimentalWasmDsl::class) wasmJs { outputModuleName = "cql" }

    sourceSets {
        commonMain {
            kotlin {
                srcDir(generateModelInfoKotlinSource)
                srcDir(generateKotlinGrammarSource)
                srcDir(inlineSystemModelInfo)
            }
            dependencies {
                api(project(":shared"))
                api("com.strumenta:antlr-kotlin-runtime:1.0.12")
            }
        }
        jvmMain { dependencies { api("com.strumenta:antlr-kotlin-runtime-jvm:1.0.12") } }
        jvmTest {
            dependencies {
                implementation(project(":quick"))
                implementation(project(":qdm"))
            }
        }
    }
}

dependencies { kover(project(":shared")) }
