plugins {
    id("cql.library-conventions")
}

// bug fix for the hapi-bom
configurations.all {
    resolutionStrategy {
        eachDependency {
            if (requested.group == "org.eclipse.jetty") {
                useVersion("11.0.20")
                because("jetty 12 is java 17")
            }
        }
    }
}


dependencies {

    api(platform("ca.uhn.hapi.fhir:hapi-fhir-bom:${project.findProperty("hapi.version")}"))

    implementation("ca.uhn.hapi.fhir:hapi-fhir-base") {
        exclude(group = "org.eclipse.jetty")
        exclude(group = "xpp3")
        exclude(group = "org.junit")
    }

    implementation("ca.uhn.hapi.fhir:hapi-fhir-converter")
    implementation("ca.uhn.hapi.fhir:hapi-fhir-structures-hl7org-dstu2")
    implementation("ca.uhn.hapi.fhir:hapi-fhir-structures-dstu2")
    implementation("ca.uhn.hapi.fhir:hapi-fhir-structures-dstu3")
    implementation("ca.uhn.hapi.fhir:hapi-fhir-structures-r4")
    implementation("ca.uhn.hapi.fhir:hapi-fhir-structures-r5")

    // This is to align with the FHIR core dependencies
    // Note that this dependency hasn"t been updated since 2013
    // we probably need to standardize on a fork up the dependency chain
    implementation("org.ogce:xpp3:1.1.6") {
        exclude(group = "org.junit")
        exclude(group = "org.hamcrest")
    }
}