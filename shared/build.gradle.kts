import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins { id("cql.kotlin-multiplatform-conventions") }

kotlin {
    js { outputModuleName = "shared" }

    @OptIn(ExperimentalWasmDsl::class) wasmJs { outputModuleName = "shared" }

    sourceSets {
        commonMain { dependencies { implementation(libs.kotlin.bignum) } }
        jsMain { dependencies { implementation(npm("saxes", "6.0.0")) } }
        wasmJsMain {
            dependencies {
                implementation(libs.kotlinx.browser)
                implementation(npm("saxes", "6.0.0"))
            }
        }
    }
}
