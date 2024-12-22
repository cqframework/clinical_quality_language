plugins {
    id("cql.library-conventions")
}

dependencies {
    api(project(":cql"))
    api(project(":model"))
    api(project(":elm"))
    testImplementation(project(":elm-xmlutil"))
    testImplementation(project(":model-xmlutil"))
    testImplementation(project(":quick"))
    testImplementation(project(":qdm"))
    testImplementation(project(":ucum"))
    testImplementation("com.tngtech.archunit:archunit:1.2.1")
    testImplementation("org.xmlunit:xmlunit-assertj:2.10.0")
    testImplementation("org.skyscreamer:jsonassert:1.5.1")
}