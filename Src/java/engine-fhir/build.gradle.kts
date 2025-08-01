plugins {
    id("cql.library-conventions")
    id("cql.xjc-conventions")
    id("cql.fhir-conventions")
}

dependencies {
    api(project(":engine"))
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
        "${projectDir}/../elm/src/main/java",
        "${projectDir}/../cql-to-elm/src/main/java",
        "${projectDir}/../engine/src/main/java",
        "${projectDir}/../engine-fhir/src/main/java",
    ))

    classDirectories.setFrom(files(
        "${projectDir}/../elm/build/classes/java/main",
        "${projectDir}/../cql-to-elm/build/classes/java/main",
        "${projectDir}/../engine/build/classes/java/main",
        "${projectDir}/../engine-fhir/build/classes/java/main",
    ))
}