plugins {
    id("com.vanniktech.maven.publish")
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
    publishToMavenCentral(true)
    if (!version.toString().endsWith("SNAPSHOT")) {
        signAllPublications()
    }
    coordinates(project.group.toString(), project.name, project.version.toString())
    pom {
        name = project.name
        description = "The ${project.name} library for the Clinical Quality Language Java reference implementation"
        url = "http://cqframework.org"

        licenses {
            license {
                name = "The Apache License, Version 2.0"
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
