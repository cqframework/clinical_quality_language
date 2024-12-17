plugins {
    id("cql.library-conventions")
    id("cql.xjc-conventions")
}

dependencies {
    api(project(":cql-to-elm"))
    api(project(":elm"))
    api(project(":model"))
}

tasks.register<XjcTask>("generateQuick") {
    schema = "${projectDir}/schema/v1.4/quick.xsd"
    binding = "${projectDir}/schema/v1.4/quick-binding.xjb"
}