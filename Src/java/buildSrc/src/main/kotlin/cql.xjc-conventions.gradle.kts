import gradle.kotlin.dsl.accessors._0b32d33f6c69498be20e3f9cff448a61.compileJava
import gradle.kotlin.dsl.accessors._0b32d33f6c69498be20e3f9cff448a61.compileKotlin

plugins {
    id("java-library")
}

val xjc by configurations.creating

dependencies {
    xjc("codes.rafael.jaxb2_commons:jaxb2-basics-ant:3.0.0")
    xjc("codes.rafael.jaxb2_commons:jaxb2-basics:3.0.0")
    xjc("codes.rafael.jaxb2_commons:jaxb2-fluent-api:3.0.0")
    // Eclipse has taken over all Java EE reference components
    // https://www.infoworld.com/article/3310042/eclipse-takes-over-all-java-ee-reference-components.html
    // https://wiki.eclipse.org/Jakarta_EE_Maven_Coordinates
    xjc("jakarta.xml.bind:jakarta.xml.bind-api:4.0.1")
    xjc("org.glassfish.jaxb:jaxb-xjc:3.0.2")
    xjc("org.glassfish.jaxb:jaxb-runtime:4.0.3")
    xjc("org.eclipse.persistence:org.eclipse.persistence.moxy:4.0.2")
    xjc("org.slf4j:slf4j-simple:1.7.36")
    xjc("org.apache.ant:ant:1.10.14")

    api("jakarta.xml.bind:jakarta.xml.bind-api:4.0.1")
    api("codes.rafael.jaxb2_commons:jaxb2-basics-runtime:3.0.0")
}

var buildDir = project.layout.buildDirectory.get().toString()
val destDir = "${buildDir}/generated/sources/$name/main/java"

tasks.compileKotlin {
    dependsOn(tasks.withType<XjcTask>())
}

tasks.compileJava {
    dependsOn(tasks.withType<XjcTask>())
}

tasks.withType<XjcTask>().configureEach {
    outputDir = destDir
}

tasks.named("sourcesJar") {
    dependsOn(tasks.withType<XjcTask>())
}

tasks.named("kotlinSourcesJar") {
    dependsOn(tasks.withType<XjcTask>())
}

sourceSets {
    main {
        java {
            srcDir(destDir)
        }
    }
}

tasks.named<Delete>("clean") {
    delete(destDir)
}
