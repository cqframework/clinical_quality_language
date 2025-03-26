plugins {
    id("cql.java-conventions")
    id("cql.library-conventions")
    id("application")
}

application {
    mainClass = "org.cqframework.cql.tools.xsd2modelinfo.Main"
}

dependencies {
    api(project(":cql-to-elm"))
    implementation("org.jetbrains.kotlinx:kotlinx-io-core-jvm:0.6.0")
    implementation("net.sf.jopt-simple:jopt-simple:4.7")
    implementation("org.apache.ws.xmlschema:xmlschema-core:2.2.5")
    implementation("org.apache.ws.xmlschema:xmlschema-walker:2.2.5")
}