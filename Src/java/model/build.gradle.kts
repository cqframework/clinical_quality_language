plugins {
    id("cql.library-conventions")
    id("cql.xjc-conventions")
}

dependencies {
    implementation("org.apache.commons:commons-text:1.10.0")
}

tasks.register<XjcTask>("generateModel") {
    schemaDir = "${projectDir}/../../cql-lm/schema"
    extraArgs = listOf("-npa")
}