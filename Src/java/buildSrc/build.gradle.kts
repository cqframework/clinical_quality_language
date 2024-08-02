plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("net.ltgt.gradle:gradle-errorprone-plugin:3.1.0")
    implementation("ru.vyarus:gradle-animalsniffer-plugin:1.7.0")
    implementation("com.github.spotbugs.snom:spotbugs-gradle-plugin:5.0.14")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.23.3")
}