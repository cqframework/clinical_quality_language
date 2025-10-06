import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar

plugins {
    kotlin("jvm")
    id("cql.maven-publishing-conventions")
    id("jacoco")
    id("org.jetbrains.dokka")
    id("io.gitlab.arturbosch.detekt")
    id("org.openrewrite.rewrite")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://central.sonatype.com/repository/maven-snapshots/")
    }
}

// Reenable once we have time to address the issues
//detekt {
//    // Applies the config files on top of detekt's default config.
//    buildUponDefaultConfig = true
//
//    // The directories where detekt looks for source files.
//    source.setFrom(
//        "src/main/kotlin",
//        "src/test/kotlin",
//    )
//
//    // Custom config with overrides.
//    config.setFrom("$rootDir/config/detekt/detekt.yml")
//}


kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.add("-Xwarning-level=DEPRECATION:disabled")
    }
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.13")

    rewrite(project(":rewrite"))

    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("uk.co.datumedge:hamcrest-json:0.2")
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.slf4j:slf4j-simple:2.0.13")

    // These are JAXB dependencies excluded because the libraries need to work
    // on Android. But for test purposes we use them pretty much everywhere.
    testRuntimeOnly("org.eclipse.persistence:org.eclipse.persistence.moxy:4.0.2")
    testRuntimeOnly("org.eclipse.parsson:parsson:1.1.5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("html-docs")
}

jacoco {
    toolVersion = "0.8.11"

}

tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        excludes = listOf("org/hl7/fhir/**")
    }

    useJUnitPlatform()
    testLogging {
        events("skipped", "failed")
    }
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
    }
    dependsOn(tasks.test)// tests are required to run before generating the report
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

mavenPublishing {
    configure(JavaLibrary(JavadocJar.Javadoc(), true))
}