plugins {
    id("cql.library-conventions")
    id("application")
}

application { mainClass = "org.cqframework.cql.tools.xsd2modelinfo.Main" }

dependencies {
    api(project(":cql"))
    implementation(libs.jopt.simple)
    implementation(libs.apache.xmlschema.core)
    implementation(libs.apache.xmlschema.walker)
}
