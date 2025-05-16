plugins {
    id("cql.kotlin-multiplatform-conventions")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("com.ionspin.kotlin:bignum:0.3.10")
            }
        }
        wasmJsMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-browser:0.3")
            }
        }
    }
}