plugins {
    id("cql.library-conventions")
    kotlin("plugin.serialization")
}

dependencies {
    api(project(":model"))
    api(project(":elm"))

    implementation("io.github.pdvrieze.xmlutil:core:0.90.3")
    implementation("io.github.pdvrieze.xmlutil:serialization:0.90.3")
    implementation("io.github.pdvrieze.xmlutil:serialization-jvm:0.90.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}
