plugins { kotlin("jvm") }

repositories { mavenCentral() }

dependencies {
    implementation(libs.openrewrite.core)
    implementation(libs.openrewrite.kotlin)
    implementation(libs.openrewrite.java)

    testImplementation(libs.openrewrite.test)
    testImplementation(kotlin("test"))
}

tasks.test { failOnNoDiscoveredTests = false }
