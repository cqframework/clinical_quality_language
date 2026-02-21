plugins {
    id("cql.library-conventions")
    id("cql.xjc-conventions")
    id("cql.fhir-conventions")
}

dependencies {
    api(project(":engine"))
    api(project(":ucum"))
    testImplementation("org.wiremock:wiremock:3.9.1")
    testImplementation(project(":cql-to-elm"))
    testImplementation(project(":quick"))
    testImplementation("ca.uhn.hapi.fhir:hapi-fhir-client")
}

val generateFhirPathTests = tasks.register<XjcTask>("generateFhirPathTests") {
    schema = "${projectDir}/src/test/resources/org/hl7/fhirpath/testSchema"
    extraArgs = listOf("-npa", "-p", "org.hl7.fhirpath.tests")
}

tasks.named("sourcesJar") {
    dependsOn(generateFhirPathTests)
}

tasks.jacocoTestReport {
    sourceDirectories.setFrom(files(
        "${projectDir}/../elm/src/commonMain/kotlin",
        "${projectDir}/../cql-to-elm/src/commonMain/kotlin",
        "${projectDir}/../engine/src/main/kotlin",
        "${projectDir}/../engine-fhir/src/main/kotlin",
    ))

    classDirectories.setFrom(files(
        "${projectDir}/../elm/build/classes/kotlin/jvm/main",
        "${projectDir}/../cql-to-elm/build/classes/kotlin/jvm/main",
        "${projectDir}/../engine/build/classes/kotlin/main",
        "${projectDir}/../engine-fhir/build/classes/kotlin/main",
    ))
}