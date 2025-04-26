plugins {
    id("cql.kotlin-multiplatform-conventions")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":cql-to-elm"))
                api(project(":serialization"))
            }
        }
    }
}
