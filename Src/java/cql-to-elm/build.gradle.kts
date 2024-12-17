plugins {
    id("cql.library-conventions")
}

dependencies {
    api(project(":cql"))
    api(project(":model"))
    api(project(":elm"))
    testImplementation(project(":elm-jackson"))
    testImplementation(project(":model-jackson"))
    testImplementation(project(":quick"))
    testImplementation(project(":qdm"))
    testImplementation(project(":ucum"))
    testImplementation("com.github.reinert:jjschema:1.16")
    testImplementation("com.tngtech.archunit:archunit:1.2.1")
    testImplementation("org.skyscreamer:jsonassert:1.5.1")
}