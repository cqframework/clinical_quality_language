plugins {
    id("cql.library-conventions")
    id("cql.xjc-temp-conventions")
    id("cql.xsd-java-gen-conventions")
}

tasks.register<XjcTask>("generateModel") {
    schema = "${projectDir}/../../cql-lm/schema/model/modelinfo.xsd"
    extraArgs = listOf("-npa")
}