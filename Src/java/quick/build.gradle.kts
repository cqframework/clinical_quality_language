plugins {
    id("cql.library-conventions")
    id("cql.xjc-conventions")
}

dependencies {
    api(project(":elm"))
    api(project(":model"))
}

tasks.register<XjcTask>("generateQuick") {
    schemaDir = "${projectDir}/schema/"
}