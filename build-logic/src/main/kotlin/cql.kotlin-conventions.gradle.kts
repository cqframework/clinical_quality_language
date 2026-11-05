import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SourcesJar

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("cql.maven-publishing-conventions")
    alias(libs.plugins.jacoco)
    alias(libs.plugins.dokka)
    alias(libs.plugins.detekt)
    alias(libs.plugins.openrewrite)
    alias(libs.plugins.kotlinx.serialization)
}

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://central.sonatype.com/repository/maven-snapshots/") }
}

detekt {
    buildUponDefaultConfig = true

    source.setFrom("src/main/kotlin", "src/test/kotlin")

    config.setFrom("$rootDir/config/detekt/detekt.yml")
    baseline = file("$projectDir/config/detekt-baseline.xml")
}

kotlin {
    jvmToolchain(17)
    compilerOptions { freeCompilerArgs.add("-Xwarning-level=DEPRECATION:disabled") }
}

dependencies {
    implementation(libs.slf4j.api)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.hamcrest.all)
    testImplementation(libs.hamcrest.json)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.slf4j.simple)
    testImplementation(libs.kotlin.test)

    // These are JAXB dependencies excluded because the libraries need to work
    // on Android. But for test purposes we use them pretty much everywhere.
    testRuntimeOnly(libs.moxy)
    testRuntimeOnly(libs.parsson)
    testRuntimeOnly(libs.junit.platform.launcher)

    rewrite(project(":tools:rewrite"))
}

tasks.jar {
    manifest {
        attributes["Implementation-Title"] = project.name
        attributes["Implementation-Version"] = project.version
        attributes["Specification-Title"] = "HL7 Clinical Quality Language (CQL)"
        attributes["Specification-Version"] = project.findProperty("specification.version") ?: ""
    }
}

tasks.register<Jar>("dokkaHtmlJar") {
    dependsOn(tasks.named("dokkaGeneratePublicationHtml"))
    from(tasks.named("dokkaGeneratePublicationHtml").map { it.outputs.files })
    archiveClassifier.set("html-docs")
}

jacoco { toolVersion = "0.8.11" }

tasks.withType<Test> {
    configure<JacocoTaskExtension> { excludes = listOf("org/hl7/fhir/**") }

    useJUnitPlatform()
    testLogging { events("skipped", "failed") }
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    reports { xml.required = true }
    dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.javadoc {
    options {
        val standardOptions = this as StandardJavadocDocletOptions
        standardOptions.addStringOption("Xdoclint:none", "-quiet")
        standardOptions.addBooleanOption("html5", true)
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:unchecked")
    options.isDeprecation = true
}

mavenPublishing { configure(JavaLibrary(JavadocJar.Javadoc(), SourcesJar.Sources())) }
