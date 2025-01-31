plugins {
    id("cql.xsd-kotlin-multiplatform-gen-conventions")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":model"))
            }
        }
        jsMain {
            dependencies {
                implementation("io.github.gciatto:kt-math-js:0.10.0")
            }
        }
        jvmTest {
            dependencies {
                implementation("org.jeasy:easy-random-core:5.0.0")
                implementation("com.tngtech.archunit:archunit:1.2.1")
            }
        }
    }
}