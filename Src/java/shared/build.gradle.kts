import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("cql.kotlin-multiplatform-conventions")
}

kotlin {
    js {
        outputModuleName = "shared"
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "shared"
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("com.ionspin.kotlin:bignum:0.3.10")
            }
        }
        jsMain {
            dependencies {
                implementation(npm("saxes", "6.0.0"))
            }
        }
        wasmJsMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-browser:0.3")
                implementation(npm("saxes", "6.0.0"))
            }
        }
    }
}