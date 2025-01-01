plugins {
    id("cql.library-conventions")
}

dependencies {
    api(project(":cql"))
    api(project(":model"))
    api(project(":elm"))

    implementation("org.jetbrains.kotlinx:kotlinx-io-core-jvm:0.6.0")

    // Temporary until we can get rid of the dependency on wrapping
    // the CQL annotations in a JAXBElement for narrative generation
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.1")

    testImplementation(project(":elm-xmlutil"))
    testImplementation(project(":model-xmlutil"))
    testImplementation(project(":quick"))
    testImplementation(project(":qdm"))
    testImplementation(project(":ucum"))
    testImplementation("com.tngtech.archunit:archunit:1.2.1")
}