plugins {
    id("cql.kotlin-multiplatform-conventions")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":cql"))
                api(project(":model"))
                api(project(":elm"))
                implementation(project(":elm-xmlutil"))
                implementation(project(":model-xmlutil"))
            }
        }
        jvmTest {
            dependencies {
                implementation(project(":quick"))
                implementation(project(":qdm"))
                implementation(project(":ucum"))
                implementation("com.tngtech.archunit:archunit:1.2.1")
            }
        }
    }
}