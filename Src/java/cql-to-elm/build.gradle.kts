plugins {
    id("cql.library-conventions")
}

dependencies {
    api(project(":cql"))
    api(project(":model"))
    api(project(":elm"))

    implementation("org.jetbrains.kotlinx:kotlinx-io-core-jvm:0.6.0")

    testImplementation(project(":elm-xmlutil"))
    testImplementation(project(":model-xmlutil"))
    testImplementation(project(":quick"))
    testImplementation(project(":qdm"))
    testImplementation(project(":ucum"))
    testImplementation("com.tngtech.archunit:archunit:1.2.1")
}