plugins {
    id("cql.xjc-conventions")
    id("cql.fhir-kotlin-multiplatform-conventions")
}

val generateFhirPathTests =
    tasks.register<XjcTask>("generateFhirPathTests") {
        outputDir.set(
            project.layout.buildDirectory.dir("generated/sources/engine-fhir/jvmTest/java")
        )
        schema.set("${projectDir}/src/jvmTest/resources/org/hl7/fhirpath/testSchema")
        extraArgs.set(listOf("-npa", "-p", "org.hl7.fhirpath.tests"))
    }

kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                api(project(":engine"))
                api(project(":ucum"))
            }
        }

        jvmTest {
            dependencies {
                implementation("org.wiremock:wiremock:3.9.1")
                implementation(project(":cql-to-elm"))
                implementation(project(":quick"))
                implementation("ca.uhn.hapi.fhir:hapi-fhir-client")

                xjcRuntimeDeps.forEach { implementation(it) }
            }
        }
    }
}

java {
    sourceSets { named("jvmTest") { java { srcDir(generateFhirPathTests.map { it.outputDir }) } } }
}

dependencies { kover(project(":engine")) }
