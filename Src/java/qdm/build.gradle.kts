plugins {
    id("cql.library-conventions")
    id("cql.xjc-conventions")
}

dependencies {
    api(project(":elm"))
    api(project(":model"))
}

tasks.register<XjcTask>("generateQdm") {
    schema = "${projectDir}/schema/qdm.xsd"
}

tasks.register<XjcTask>("generateQdm42") {
    schema = "${projectDir}/schema/qdm.4.2.xsd"
}