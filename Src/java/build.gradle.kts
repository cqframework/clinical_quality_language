plugins {
    kotlin("jvm")
    id("com.vanniktech.maven.publish") apply false
    id("org.sonarqube") version "7.2.2.6593"
    id("com.dorongold.task-tree") version "2.1.1"
    id("com.diffplug.spotless") version "8.0.0"
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