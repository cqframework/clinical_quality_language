import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("cql.xsd-kotlin-multiplatform-gen-conventions")
}

kotlin {
    js {
        outputModuleName = "elm"
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "elm"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":shared"))
            }
        }
        jvmTest {
            dependencies {
                implementation(project(":cql-to-elm"))
                implementation(project(":ucum"))
                implementation(project(":quick"))
                implementation("org.jeasy:easy-random-core:5.0.0")
                implementation("com.tngtech.archunit:archunit:1.2.1")
                implementation("org.xmlunit:xmlunit-assertj:2.10.0")
                implementation("org.skyscreamer:jsonassert:1.5.1")
            }
        }
    }
}