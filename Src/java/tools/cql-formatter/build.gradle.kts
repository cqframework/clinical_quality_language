plugins {
    id("cql.kotlin-conventions")
    id("application")
}

application {
    mainClass = "org.cqframework.cql.tools.formatter.Main"
}

dependencies {
    testImplementation(project(":cql-to-elm"))
    implementation(project(":cql"))
}

sourceSets {
    test {
        resources {
            srcDir("../../cql-to-elm/src/test/resources")
        }
    }
}