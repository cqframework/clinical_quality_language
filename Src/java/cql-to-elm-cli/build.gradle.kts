plugins {
    id("cql.java-conventions")
    id("application")
}

application {
    mainClass = "org.cqframework.cql.cql2elm.cli.Main"
}

dependencies {
    implementation(project(":cql-to-elm"))
    // Is this needed once JAXB and Jackson are replaced with XmlUtil?
    // implementation(project(":cql-to-elm-jackson"))
    implementation(project(":quick"))
    implementation(project(":qdm"))
    implementation(project(":model-xmlutil"))
    implementation(project(":elm-xmlutil"))
    implementation(project(":ucum"))
    implementation("net.sf.jopt-simple:jopt-simple:4.7")
    implementation("org.slf4j:slf4j-simple:2.0.13")
    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.5")
    implementation("org.eclipse.persistence:org.eclipse.persistence.moxy:4.0.2")
    testImplementation(project(":model-xmlutil"))
    testImplementation(project(":elm-xmlutil"))
}