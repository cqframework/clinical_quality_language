plugins {
    id("cql.library-conventions")
}

dependencies {
    api(project(":model"))

    implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.6.0")
    implementation("io.github.pdvrieze.xmlutil:core:0.90.4-SNAPSHOT")
    implementation("io.github.pdvrieze.xmlutil:serialization:0.90.4-SNAPSHOT")
    implementation("io.github.pdvrieze.xmlutil:serialization-jvm:0.90.4-SNAPSHOT")

    testImplementation(project(":quick"))
    testImplementation(project(":qdm"))
}