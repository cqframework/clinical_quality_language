import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("cql.kotlin-multiplatform-conventions")
}

kotlin {
    js {
        outputModuleName = "engine"
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "engine"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":cql-to-elm"))
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
            }
        }
        jvmMain {
            dependencies {
                api("org.apache.commons:commons-text:1.10.0")
            }
        }
        jvmTest {
            dependencies {
                implementation(project(":ucum"))
                implementation("org.mockito:mockito-core:5.4.0")
            }
        }
    }
}