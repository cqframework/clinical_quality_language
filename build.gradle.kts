plugins {
    id("cqf.git-version")
    id("cqf.spotless-conventions")
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.task.tree)
}

sonar {
    properties {
        property("sonar.projectKey", "cqframework_clinical_quality_language")
        property("sonar.organization", "cqframework")
        property("sonar.host.url", "https://sonarcloud.io")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "**/build/reports/kover/report.xml,**/build/reports/jacoco/test/jacocoTestReport.xml",
        )
    }
}

repositories { mavenCentral() }
