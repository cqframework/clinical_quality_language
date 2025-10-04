plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.openrewrite:rewrite-core:8.62.6")
    implementation("org.openrewrite:rewrite-kotlin:8.62.6")
    implementation("org.openrewrite:rewrite-java-17:8.62.6")

    testImplementation("org.openrewrite:rewrite-test:8.62.6")
    testImplementation(kotlin("test"))
}