plugins {
    kotlin("jvm")
    id("maven-publish")
    id("jacoco")
    id("signing")
    id("cql.sca-conventions")
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


/*
A few things:
   - You must have an OSSRH Jira account (https://issues.sonatype.org/secure/Signup!valault.jspa)
   - Your account must have privileges to upload info.cqframework artifacts (https://issues.sonatype.org/browse/OSSRH-15514)
   - You must have a gpg key (http://central.sonatype.org/pages/working-with-pgp-signatures.html)
   - You must set your account info and GPG key in your user"s gradle.properties file.  For example:
       ossrhUsername=foo
       ossrhPassword=b@r
       signing.keyId=24875D73
       signing.password=secret
       signing.secretKeyRingFile=/Users/me/.gnupg/secring.gpg
   - If the library version ends with "-SNAPSHOT", it will be deployed to the snapshot repository, else it will be
     deployed to the staging repository (which you then must manually release http://central.sonatype.org/pages/releasing-the-deployment.html).
   - Repo for snapshots and releases for the translator modules: https://oss.sonatype.org/content/groups/public/info/cqframework/
   - Repo for snapshots, releases, and staged releases for the translator modules: https://oss.sonatype.org/content/groups/staging/info/cqframework/
   - Repo for snapshots and releases for the engine modules: https://oss.sonatype.org/content/groups/public/org/opencds/cqf/cql/
   - Repo for snapshots, releases, and staged releases for the engine modules: https://oss.sonatype.org/content/groups/staging/org/opencds/cqf/cql/
 */
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name = project.name
                packaging = "jar"
                description =
                    "The ${project.name} library for the Clinical Quality Language Java reference implementation"
                url = "http://cqframework.info"

                scm {
                    connection = "scm:git:git@github.com:cqframework/clinical_quality_language.git"
                    developerConnection = "scm:git:git@github.com:cqframework/clinical_quality_language.git"
                    url = "git@github.com:cqframework/clinical_quality_language.git"
                }

                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
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
                }
            }
        }
    }
    repositories {
        maven {
            credentials {
                username = project.findProperty("ossrhUsername") as String? ?: System.getenv("OSSRH_USERNAME") ?: ""
                password = project.findProperty("ossrhPassword") as String? ?: System.getenv("OSSRH_TOKEN") ?: ""
            }

            /* Use these to test locally (but don"t forget to comment out others!)
            val releasesRepoUrl = "file://${buildDir}/repo"
            val snapshotsRepoUrl = "file://${buildDir}/ssRepo"
            */

            // change URLs to point to your repos, e.g. http://my.org/repo
            val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
            if ((project.version as String).endsWith("SNAPSHOT")) {
                url = uri(snapshotsRepoUrl)
            } else {
                url = uri(releasesRepoUrl)
            }
        }
    }
}

signing {
    if (!(version as String).endsWith("SNAPSHOT")) {
        sign(publishing.publications["mavenJava"])
    }
}