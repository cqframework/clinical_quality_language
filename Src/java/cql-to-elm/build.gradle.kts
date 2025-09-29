import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("cql.kotlin-multiplatform-conventions")
}

kotlin {
    js {
        outputModuleName = "cql-to-elm"
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "cql-to-elm"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":cql"))
                api(project(":elm"))
            }
        }
        jvmTest {

            dependencies {
                implementation(project(":quick"))
                implementation(project(":qdm"))
                implementation(project(":ucum"))
                implementation("com.tngtech.archunit:archunit:1.2.1")
            }
        }
    }
}