plugins { id("cql.fhir-conventions") }

dependencies {
    implementation(project(":cql-to-elm"))
    implementation(project(":cqf-fhir"))
    implementation(libs.gson)
    implementation(libs.apache.commons.compress)
}
