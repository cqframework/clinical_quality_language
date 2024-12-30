plugins {
    id("cql.xsd-kotlin-multiplatform-gen-conventions")
}


kotlin {
    sourceSets {
        jvmTest {
            dependencies {
                implementation(project(":model-xmlutil"))
            }
        }
    }
}