import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("cql.fhir-kotlin-multiplatform-conventions")
    id("cql.xjc-kotlin-multiplatform-conventions")
}

val generateFhirPathTests =
    tasks.register<XjcTask>("generateFhirPathTests") {
        schema.set("${projectDir}/src/jvmTest/resources/org/hl7/fhirpath/testSchema")
        extraArgs.set(listOf("-npa", "-p", "org.hl7.fhirpath.tests"))
    }

kotlin {
    js { outputModuleName = "engine-fhir" }

    @OptIn(ExperimentalWasmDsl::class) wasmJs { outputModuleName = "engine-fhir" }

    sourceSets {
        commonMain { dependencies { api(project(":engine")) } }

        jvmMain { dependencies { api(project(":ucum")) } }

        jvmTest {
            dependencies {
                implementation("org.wiremock:wiremock:3.9.1")
                implementation(project(":cql-to-elm"))
                implementation(project(":quick"))
                implementation("ca.uhn.hapi.fhir:hapi-fhir-client")
            }
        }
    }
}

java {
    sourceSets { named("jvmMain") { java { srcDir(generateFhirPathTests.map { it.outputDir }) } } }
}

dependencies { kover(project(":engine")) }
