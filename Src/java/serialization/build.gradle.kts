plugins {
    id("cql.kotlin-multiplatform-conventions")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":elm"))
                api(project(":cql"))
            }
        }

        jvmTest {
            dependencies {
                implementation(project(":cql-to-elm"))
                implementation(project(":ucum"))
                implementation(project(":quick"))
                implementation(project(":qdm"))
                implementation("org.xmlunit:xmlunit-assertj:2.10.0")
                implementation("org.skyscreamer:jsonassert:1.5.1")
            }
        }
    }
}