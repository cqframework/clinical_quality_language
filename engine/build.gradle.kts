import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins { id("cql.kotlin-multiplatform-conventions") }

kotlin {
    js { outputModuleName = "engine" }

    @OptIn(ExperimentalWasmDsl::class) wasmJs { outputModuleName = "engine" }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":cql-to-elm"))
                implementation(libs.kotlinx.datetime)
            }
        }
        jvmMain { dependencies { api(libs.apache.commons.text) } }
        jsMain { dependencies { implementation(npm("@js-joda/timezone", "2.23.0")) } }
        wasmJsMain { dependencies { implementation(npm("@js-joda/timezone", "2.23.0")) } }
        jvmTest {
            dependencies {
                implementation(project(":ucum"))
                implementation(libs.mockito.core)
            }
        }
    }
}

dependencies { kover(project(":cql-to-elm")) }
