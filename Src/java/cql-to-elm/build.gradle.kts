plugins {
    id("cql.library-conventions")
    id("cql.xjc-conventions")
}

dependencies {
    api(project(":cql"))
    api(project(":model"))
    api(project(":elm"))
    api("org.fhir:ucum:1.0.8")
    api("org.apache.commons:commons-text:1.10.0")

    // TODO: This dependencies are required due the the fact that the CqlTranslatorOptionsMapper lives
    // in the cql-to-elm project. Ideally, we"d factor out all serialization dependencies into common
    // libraries such that we could swap out jackson for something else. In the meantime, these are
    // "implementation" dependencies so that they are not exported downstream.
    implementation("com.fasterxml.jackson.module:jackson-module-jakarta-xmlbind-annotations:${project.findProperty("jackson.version")}")
    testImplementation(project(":elm-jackson"))
    testImplementation(project(":model-jackson"))
    testImplementation(project(":quick"))
    testImplementation(project(":qdm"))
    testImplementation("com.github.reinert:jjschema:1.16")
    testImplementation("com.tngtech.archunit:archunit:1.2.1")
    testImplementation("org.skyscreamer:jsonassert:1.5.1")
}