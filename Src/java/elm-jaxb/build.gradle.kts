plugins {
    id("cql.library-conventions")
}

dependencies {
    api(project(":elm"))
    testImplementation(project(":cql-to-elm"))
    testImplementation(project(":ucum"))
    testImplementation(project(":model-jaxb"))
}
