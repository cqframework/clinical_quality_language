plugins {
    id("cql.kotlin-multiplatform-conventions")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":model"))

                implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.6.0")
                implementation("io.github.pdvrieze.xmlutil:core:0.90.3")
                implementation("io.github.pdvrieze.xmlutil:serialization:0.90.3")
            }
        }

        jvmMain {
            dependencies {
                implementation("io.github.pdvrieze.xmlutil:serialization-jvm:0.90.3")
            }
        }

        jvmTest {
            dependencies {
                implementation(project(":quick"))
                implementation(project(":qdm"))
            }
        }
    }
}