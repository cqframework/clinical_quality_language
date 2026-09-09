plugins {
    id("cql.library-conventions")
    id("cql.fhir-conventions")
}

dependencies {
    api(project(":cql-to-elm"))
    api(project(":engine"))
    api(project(":engine-fhir"))

    testImplementation(project(":quick"))
    testImplementation(libs.reflections)
}
