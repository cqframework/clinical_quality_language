plugins {
    id("cql.java-conventions")
    id("application")
    id("antlr")
}

dependencies {
    antlr("org.antlr:antlr4:${project.findProperty("antlr.version")}")
    api("org.antlr:antlr4-runtime:${project.findProperty("antlr.version")}")
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

tasks.sourcesJar {
    from(tasks.generateGrammarSource)
}

tasks.generateGrammarSource {
    val buildDir = project.layout.buildDirectory.get().toString()
    outputDirectory = file("${buildDir}/generated/sources/antlr/main/java/org/cqframework/cql/gen")
    arguments = listOf("-visitor", "-package", "org.cqframework.cql.gen")
}