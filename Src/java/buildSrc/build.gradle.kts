plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.20")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.3")
    implementation("net.ltgt.gradle:gradle-errorprone-plugin:3.1.0")
    implementation("ru.vyarus:gradle-animalsniffer-plugin:1.7.1")
    implementation("com.github.spotbugs.snom:spotbugs-gradle-plugin:5.0.14")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.25.0")
}

kotlin {
    jvmToolchain(17)
}