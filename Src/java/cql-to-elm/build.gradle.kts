plugins {
    id("cql.library-conventions")
}

dependencies {
    api(project(":cql"))
    api(project(":model"))
    api(project(":elm"))
    testImplementation(project(":elm-jaxb"))
    testImplementation(project(":model-jaxb"))
    testImplementation(project(":quick"))
    testImplementation(project(":qdm"))
    testImplementation(project(":ucum"))
    testImplementation("com.tngtech.archunit:archunit:1.2.1")
}