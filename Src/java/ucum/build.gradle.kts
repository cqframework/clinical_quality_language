plugins {
    id("cql.library-conventions")
}

dependencies {
    api(project(":cql-to-elm"))
    api("org.fhir:ucum:1.0.8")
}