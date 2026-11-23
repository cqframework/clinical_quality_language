plugins {
    id("cql.library-conventions")
    id("cql.fhir-common-conventions")
}

dependencies {
    api(platform(libs.hapi.fhir.bom))

    implementation(libs.hapi.fhir.base) {
        exclude(group = "org.eclipse.jetty")
        exclude(group = "xpp3")
        exclude(group = "org.junit")
    }

    // org.hl7.fhir.utilities still uses Jackson 2 core, which HAPI 9 no longer brings in
    // transitively
    runtimeOnly(libs.jackson2.core)

    implementation(libs.hapi.fhir.converter)
    implementation(libs.hapi.fhir.structures.hl7org.dstu2)
    implementation(libs.hapi.fhir.structures.dstu2)
    implementation(libs.hapi.fhir.structures.dstu3)
    implementation(libs.hapi.fhir.structures.r4)
    implementation(libs.hapi.fhir.structures.r5)

    // This is to align with the FHIR core dependencies
    // Note that this dependency hasn"t been updated since 2013
    // we probably need to standardize on a fork up the dependency chain
    implementation(libs.xpp3) {
        exclude(group = "org.junit")
        exclude(group = "org.hamcrest")
    }
}
