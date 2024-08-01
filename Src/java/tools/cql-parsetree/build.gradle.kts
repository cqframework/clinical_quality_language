plugins {
    id("cql.java-conventions")
    id("application")
}

application {
    mainClass = "org.cqframework.cql.tools.parsetree.Main"
}

dependencies {
    implementation(project(":cql"))
    implementation("org.antlr:antlr4:4.10.1") {
        // antlr 4.5 includes these classes directly
        exclude(group= "org.abego.treelayout", module= "org.abego.treelayout.core")
    }
}