plugins {
    id("cql.kotlin-multiplatform-conventions")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":cql"))
                api(project(":elm"))
            }
        }
        jvmTest {
            dependencies {
                implementation(project(":quick"))
                implementation(project(":qdm"))
                implementation(project(":ucum"))
                implementation("com.tngtech.archunit:archunit:1.2.1")
                runtimeOnly(project(":serialization"))
            }
        }
    }
}