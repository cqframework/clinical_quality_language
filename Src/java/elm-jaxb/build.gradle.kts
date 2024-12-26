plugins {
    id("cql.library-conventions")
}

dependencies {
    api(project(":elm"))
    testImplementation(project(":cql-to-elm"))
    testImplementation(project(":ucum"))
    testImplementation(project(":model-jaxb"))
    testImplementation(project(":quick"))

    testImplementation("org.xmlunit:xmlunit-assertj:2.10.0")
    testImplementation("org.skyscreamer:jsonassert:1.5.1")
}
