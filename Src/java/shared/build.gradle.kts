plugins {
    id("cql.kotlin-multiplatform-conventions")
}

kotlin {
    sourceSets {
        commonMain {
            kotlin {
                srcDir("build/generated/sources/cql/commonMain/kotlin")
                srcDir("build/generated/sources/elm/commonMain/kotlin")
            }
        }
        jsMain {
            dependencies {
                implementation("io.github.gciatto:kt-math-js:0.10.0")
            }
        }
    }
}