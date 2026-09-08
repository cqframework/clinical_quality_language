plugins {
    id("cql.kotlin-conventions")
    id("application")
}

application { mainClass = "org.cqframework.cql.cql2elm.cli.Main" }

dependencies {
    implementation(platform(libs.hapi.fhir.bom))

    implementation(libs.hapi.fhir.base) {
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
    implementation(libs.jopt.simple)
    implementation(libs.slf4j.simple)
    implementation(libs.jaxb.runtime)
    implementation(libs.moxy)
    implementation(libs.hapi.fhir.structures.r5)
}
