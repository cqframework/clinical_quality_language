plugins {
    id("cql.library-conventions")
}

dependencies {
    implementation(project(":cql-to-elm"))
    implementation(project(":elm-xmlutil"))
    implementation(project(":model-xmlutil"))
    testImplementation(project(":ucum"))
}
