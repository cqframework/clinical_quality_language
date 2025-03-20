plugins {
    id("cql.library-conventions")
}

dependencies {
    implementation(project(":cql-to-elm"))
    implementation(project(":serialization"))
    testImplementation(project(":ucum"))
}
