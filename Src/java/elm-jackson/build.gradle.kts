plugins {
    id("cql.library-conventions")
}

dependencies {
    val jacksonVersion = project.findProperty("jackson.version")
    api(project(":model"))
    api(project(":elm"))
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:${jacksonVersion}")
    api("com.fasterxml.jackson.module:jackson-module-jakarta-xmlbind-annotations:${jacksonVersion}")
}
