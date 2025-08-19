plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.10")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.9.20")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.3")
    implementation("net.ltgt.gradle:gradle-errorprone-plugin:3.1.0")
    implementation("ru.vyarus:gradle-animalsniffer-plugin:1.7.2")
    implementation("com.github.spotbugs.snom:spotbugs-gradle-plugin:5.0.14")
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.33.0")
    implementation("org.jetbrains.kotlin:kotlin-serialization:2.2.10")
    implementation("org.glassfish.jaxb:xsom:4.0.5")
    implementation("com.squareup:kotlinpoet:2.1.0")
    implementation("com.github.gmazzo.buildconfig:com.github.gmazzo.buildconfig.gradle.plugin:5.6.7")
}

kotlin {
    jvmToolchain(17)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}