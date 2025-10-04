plugins {
    id("cql.kotlin-conventions")
    id("application")
}

application {
    mainClass = "org.cqframework.cql.tools.parsetree.Main"
}

dependencies {
    implementation(project(":cql"))
}