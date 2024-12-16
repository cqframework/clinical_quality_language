plugins {
    id("cql.library-conventions")
}

dependencies {
    implementation(project(":cql-to-elm"))
    implementation("com.fasterxml.jackson.module:jackson-module-jakarta-xmlbind-annotations:${project.findProperty("jackson.version")}")
}