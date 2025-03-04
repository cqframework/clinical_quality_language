plugins {
    kotlin("jvm")
    id("jacoco")
    id("cql.sca-conventions")
    id("cql.maven-conventions")
    id("com.diffplug.spotless")
    id("org.jetbrains.dokka")
    id("io.gitlab.arturbosch.detekt")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
        mavenContent {
            snapshotsOnly()
        }
    }
    maven {
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}

kotlin {
    jvmToolchain(17)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}


spotless {
    java {
        targetExclude("**/generated/**")
        palantirJavaFormat()
    }
    kotlin {
        targetExclude("**/generated/**")
        ktfmt().kotlinlangStyle()
    }
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.13")
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

tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
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
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:unchecked")
    options.isDeprecation = true
}