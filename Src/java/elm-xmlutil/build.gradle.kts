plugins {
    id("cql.library-conventions")
    kotlin("plugin.serialization")
}

dependencies {
    api(project(":model"))
    api(project(":elm"))

    implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.6.0")
    implementation("io.github.pdvrieze.xmlutil:core:0.90.4-SNAPSHOT")
    implementation("io.github.pdvrieze.xmlutil:serialization:0.90.4-SNAPSHOT")
    implementation("io.github.pdvrieze.xmlutil:serialization-jvm:0.90.4-SNAPSHOT")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    testImplementation(project(":cql-to-elm"))
    testImplementation(project(":model-xmlutil"))
    testImplementation(project(":ucum"))
    testImplementation(project(":quick"))
    testImplementation("org.xmlunit:xmlunit-assertj:2.10.0")
    testImplementation("org.skyscreamer:jsonassert:1.5.1")
}