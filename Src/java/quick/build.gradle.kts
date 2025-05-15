plugins {
    id("cql.library-conventions")
    id("cql.xjc-conventions")
}

dependencies {
    implementation(project(":cql-to-elm"))
}

tasks.register<XjcTask>("generateQuick") {
    schema = "${projectDir}/schema/v1.4/quick.xsd"
    binding = "${projectDir}/schema/v1.4/quick-binding.xjb"
}