plugins {
    id("cql.kotlin-multiplatform-conventions")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":model"))
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