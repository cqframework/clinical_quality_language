plugins {
    id("cql.java-conventions")
    id("application")
    id("antlr")
}

dependencies {
    val version = project.findProperty("antlr.version")
    antlr("org.antlr:antlr4:${version}")
    api("org.antlr:antlr4-runtime:${version}")
}

application {
    mainClass = "org.cqframework.cql.Main"
}

sourceSets {
    main {
        antlr {
            srcDir("../../grammar")
        }
        java {
            srcDir("build/generated/sources/antlr/main/java")
        }
    }
}

tasks.generateGrammarSource {
    val buildDir = project.layout.buildDirectory.get().toString()
    outputDirectory = file("${buildDir}/generated/sources/antlr/main/java/org/cqframework/cql/gen")
    arguments = listOf("-visitor", "-package", "org.cqframework.cql.gen")
}

tasks.sourcesJar {
    from(tasks.generateGrammarSource)
}