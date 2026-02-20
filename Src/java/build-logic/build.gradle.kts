plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.10")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:2.1.0")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.8")
    implementation("net.ltgt.gradle:gradle-errorprone-plugin:5.0.0")
    implementation("ru.vyarus:gradle-animalsniffer-plugin:2.0.1")
    implementation("com.github.spotbugs.snom:spotbugs-gradle-plugin:6.4.8")
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.36.0")
    implementation("org.jetbrains.kotlin:kotlin-serialization:2.3.10")
    implementation("org.glassfish.jaxb:xsom:4.0.6")
    implementation("com.squareup:kotlinpoet:2.2.0")
    implementation("com.github.gmazzo.buildconfig:com.github.gmazzo.buildconfig.gradle.plugin:6.0.7")
    implementation("org.openrewrite.rewrite:org.openrewrite.rewrite.gradle.plugin:7.26.0")
}

kotlin {
    jvmToolchain(17)
}
