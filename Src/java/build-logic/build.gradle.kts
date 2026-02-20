plugins {
    kotlin("jvm") version "2.2.10"
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.10")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:2.1.0")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.8")
    implementation("net.ltgt.gradle:gradle-errorprone-plugin:3.1.0")
    implementation("ru.vyarus:gradle-animalsniffer-plugin:2.0.1")
    implementation("com.github.spotbugs.snom:spotbugs-gradle-plugin:5.0.14")
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.36.0")
    implementation("org.jetbrains.kotlin:kotlin-serialization:2.2.10")
    implementation("org.glassfish.jaxb:xsom:4.0.5")
    implementation("com.squareup:kotlinpoet:2.1.0")
    implementation("com.github.gmazzo.buildconfig:com.github.gmazzo.buildconfig.gradle.plugin:5.6.7")
    implementation("org.openrewrite.rewrite:org.openrewrite.rewrite.gradle.plugin:7.17.0")
}

kotlin {
    jvmToolchain(17)
}
