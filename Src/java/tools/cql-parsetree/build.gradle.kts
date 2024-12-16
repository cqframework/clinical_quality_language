plugins {
    id("cql.java-conventions")
    id("application")
}

application {
    mainClass = "org.cqframework.cql.tools.parsetree.Main"
}

dependencies {
    implementation(project(":cql"))
}