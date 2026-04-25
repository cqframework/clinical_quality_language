plugins {
    kotlin("jvm")
    id("cqf.git-version")
    id("cqf.spotless-conventions")
    id("com.vanniktech.maven.publish") apply false
    id("org.sonarqube") version "7.2.2.6593"
    id("com.dorongold.task-tree") version "4.0.1"
}

sonar {
    properties {
        property("sonar.projectKey", "cqframework_clinical_quality_language")
        property("sonar.organization", "cqframework")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

repositories { mavenCentral() }
