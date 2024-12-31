plugins {
    id("cql.xsd-kotlin-multiplatform-gen-conventions")
}


kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-io-core:0.6.0")
            }
        }
        jvmTest {
            dependencies {
                implementation(project(":model-xmlutil"))
                implementation("org.hamcrest:hamcrest-all:1.3")
                implementation("uk.co.datumedge:hamcrest-json:0.2")
                implementation("org.slf4j:slf4j-simple:2.0.13")

                // These are JAXB dependencies excluded because the libraries need to work
                // on Android. But for test purposes we use them pretty much everywhere.
                runtimeOnly("org.eclipse.persistence:org.eclipse.persistence.moxy:4.0.2")
                runtimeOnly("org.eclipse.parsson:parsson:1.1.5")
            }
        }
    }
}