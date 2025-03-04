plugins {
    id("maven-publish")
    id("signing")
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