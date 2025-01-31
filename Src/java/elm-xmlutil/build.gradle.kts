plugins {
    id("cql.kotlin-multiplatform-conventions")
    kotlin("plugin.serialization")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":model"))
                api(project(":elm"))

                implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.6.0")
                implementation("io.github.pdvrieze.xmlutil:core:0.90.3")
                implementation("io.github.pdvrieze.xmlutil:serialization:0.90.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            }
        }

        jvmMain {
            dependencies {
                implementation("io.github.pdvrieze.xmlutil:serialization-jvm:0.90.3")
            }
        }

        jvmTest {
            dependencies {
                implementation(project(":cql-to-elm"))
                implementation(project(":model-xmlutil"))
                implementation(project(":ucum"))
                implementation(project(":quick"))
                implementation("org.xmlunit:xmlunit-assertj:2.10.0")
                implementation("org.skyscreamer:jsonassert:1.5.1")
            }
        }
    }
}