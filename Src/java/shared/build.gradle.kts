plugins {
    id("cql.kotlin-multiplatform-conventions")
}

kotlin {
    sourceSets {
        jsMain {
            dependencies {
                implementation("io.github.gciatto:kt-math-js:0.10.0")
            }
        }
    }
}