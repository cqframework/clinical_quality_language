plugins {
    kotlin("jvm")
    id("org.sonarqube") version "4.4.1.3373"
}

sonar {
  properties {
    property("sonar.projectKey", "cqframework_clinical_quality_language")
    property("sonar.organization", "cqframework")
    property("sonar.host.url", "https://sonarcloud.io")
  }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

kotlin {
    jvmToolchain(11)
}