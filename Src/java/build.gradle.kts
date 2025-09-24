plugins {
    kotlin("jvm")
    id("org.sonarqube") version "4.4.1.3373"
    id("com.dorongold.task-tree") version "2.1.1"
    id("com.diffplug.spotless") version "6.25.0"
}

sonar {
  properties {
    property("sonar.projectKey", "cqframework_clinical_quality_language")
    property("sonar.organization", "cqframework")
    property("sonar.host.url", "https://sonarcloud.io")
  }
}

spotless {
    java {
        targetExclude("**/generated/**")
        palantirJavaFormat()
    }
    kotlin {
        target("**/*.kt")
        targetExclude("**/generated/**", "**/generated-sources/**")
        ktfmt().kotlinlangStyle()
    }
}

repositories {
    mavenCentral()
}