plugins {
    id("cql.java-conventions")
    id("application")
}

application {
    mainClass = "org.cqframework.cql.cql2elm.cli.Main"
}

dependencies {
    implementation(project(":cql-to-elm"))
    implementation(project(":quick"))
    implementation(project(":qdm"))
    implementation(project(":model-xmlutil"))
    implementation(project(":elm-xmlutil"))
    implementation(project(":ucum"))
    implementation("org.jetbrains.kotlinx:kotlinx-io-core-jvm:0.6.0")
    implementation("net.sf.jopt-simple:jopt-simple:4.7")
    implementation("org.slf4j:slf4j-simple:2.0.13")
    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.5")
    implementation("org.eclipse.persistence:org.eclipse.persistence.moxy:4.0.2")
}