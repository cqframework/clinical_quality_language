plugins {
    id("cql.library-conventions")
}

dependencies {
    implementation(project(":cql-to-elm"))
    // implementation project(":model-jaxb")
    implementation(project(":elm-jaxb"))
    implementation(project(":model-jackson"))
    implementation(project(":elm-jackson"))
}