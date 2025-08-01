import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar

plugins {
    kotlin("jvm")
    id("com.vanniktech.maven.publish")
    id("jacoco")
    id("cql.sca-conventions")
    id("org.jetbrains.dokka")
    id("io.gitlab.arturbosch.detekt")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://central.sonatype.com/repository/maven-snapshots/")
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

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.6.0")
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


/*
A few things:
   - You must have a Maven Central account (https://central.sonatype.org/register/central-portal/)
   - Your account must have privileges to upload info.cqframework artifacts
   - You must have a gpg key (http://central.sonatype.org/pages/working-with-pgp-signatures.html)
   - You must set your account info and GPG key in your user's gradle.properties file.  For example:
       mavenCentralUsername=foo
       mavenCentralPassword=b@r
       signing.keyId=24875D73
       signing.password=secret
       signing.secretKeyRingFile=/Users/me/.gnupg/secring.gpg
   - If the library version ends with '-SNAPSHOT', it will be deployed to the snapshot repository, else it will be
     deployed to the staging repository (which you then must manually release http://central.sonatype.org/pages/releasing-the-deployment.html).
   - Repo for snapshots for the translator modules: https://central.sonatype.com/repository/maven-snapshots/info/cqframework/
   - Repo for releases for the translator modules: https://central.sonatype.com/repository/maven-releases/info/cqframework/
 */
mavenPublishing {
    configure(JavaLibrary(JavadocJar.Javadoc(), true))

    publishToMavenCentral(true)
    if (!version.toString().endsWith("SNAPSHOT")) {
        signAllPublications()
    }
    coordinates("info.cqframework", project.name, project.version.toString())
    pom {
        name = project.name
        description = "The ${project.name} library for the Clinical Quality Language Java reference implementation"
        url = "http://cqframework.org"

        licenses {
            license {
                name ="The Apache License, Version 2.0"
                url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }

        scm {
            connection = "scm:git:git@github.com:cqframework/clinical_quality_language.git"
            developerConnection = "scm:git:git@github.com:cqframework/clinical_quality_language.git"
            url = "git@github.com:cqframework/clinical_quality_language.git"
        }

        developers {
            developer {
                name = "Bryn Rhodes"
            }
            developer {
                name = "Chris Moesel"
            }
            developer {
                name = "Rob Dingwell"
            }
            developer {
                name = "Jason Walonoski"
            }
            developer {
                name = "Marc Hadley"
            }
            developer {
                name = "Jonathan Percival"
            }
            developer {
                name = "Anton Vasetenkov"
            }
            developer {
                name = "Luke deGruchy"
            }
        }
    }
}
