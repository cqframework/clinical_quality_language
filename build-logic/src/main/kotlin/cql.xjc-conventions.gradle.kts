plugins {
    kotlin("jvm")
    id("cql.xjc-common-conventions")
}

dependencies {
    api("jakarta.xml.bind:jakarta.xml.bind-api:4.0.1")
    api("codes.rafael.jaxb2_commons:jaxb2-basics-runtime:3.0.0")
}

tasks.withType<XjcTask>().configureEach {
    outputDir.set(project.layout.buildDirectory.dir("generated/sources/$name/main/java"))
}
