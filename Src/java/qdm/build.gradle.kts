plugins {
    id("cql.library-conventions")
    id("cql.xjc-conventions")
}

dependencies {
    api(project(":elm"))
    api(project(":model"))
}

tasks.register<XjcTask>("generateQdm") {
    schemaDir = "${projectDir}/schema/qdm.xsd"
}

tasks.register<XjcTask>("generateQmd42") {
    schemaDir = "${projectDir}/schema/qdm.4.2.xsd"
}
