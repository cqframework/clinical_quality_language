plugins {
    id("cql.java-conventions")
    id("application")
}

application {
    mainClass = "org.cqframework.cql.cql2elm.cli.Main"
}

dependencies {
    implementation(platform("ca.uhn.hapi.fhir:hapi-fhir-bom:${project.findProperty("hapi.version")}"))

    implementation("ca.uhn.hapi.fhir:hapi-fhir-base") {
        exclude(group = "org.eclipse.jetty")
        exclude(group = "xpp3")
        exclude(group = "org.junit")
    }

    implementation(project(":cql-to-elm"))
    implementation(project(":cqf-fhir"))
    implementation(project(":cqf-fhir-npm"))
    implementation(project(":quick"))
    implementation(project(":qdm"))
    implementation(project(":ucum"))
    implementation("org.jetbrains.kotlinx:kotlinx-io-core-jvm:0.6.0")
    implementation("net.sf.jopt-simple:jopt-simple:4.7")
    implementation("org.slf4j:slf4j-simple:2.0.13")
    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.5")
    implementation("org.eclipse.persistence:org.eclipse.persistence.moxy:4.0.2")
    implementation("ca.uhn.hapi.fhir:hapi-fhir-structures-r5")
}