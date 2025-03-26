plugins {
    id("cql.library-conventions")
}

dependencies {
    implementation(project(":cql-to-elm"))
    testImplementation(project(":ucum"))
}
