import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins { id("cql.kotlin-multiplatform-conventions") }

val generateElmKotlinSource by
    tasks.registering(XsdKotlinGenTask::class) {
        description = "Generates Kotlin sources for ELM classes."
        inputXsd.set(rootProject.layout.projectDirectory.file("schemas/elm/library.xsd"))
        outputDir.set(project.layout.buildDirectory.dir("generated/sources/elm"))
        jsExport.set(true)
    }

kotlin {
    js { outputModuleName = "elm" }

    @OptIn(ExperimentalWasmDsl::class) wasmJs { outputModuleName = "elm" }

    sourceSets {
        commonMain {
            kotlin { srcDir(generateElmKotlinSource) }

            dependencies { api(project(":shared")) }
        }
        jvmTest {
            dependencies {
                implementation(project(":cql-to-elm"))
                implementation(project(":ucum"))
                implementation(project(":quick"))
                implementation(libs.easy.random.core)
                implementation(libs.archunit)
                implementation(libs.xmlunit.assertj)
                implementation(libs.jsonassert)
            }
        }
    }
}

dependencies { kover(project(":shared")) }
