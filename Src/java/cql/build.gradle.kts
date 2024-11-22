plugins {
    id("cql.java-conventions")
    id("antlr")
}

dependencies {
    val version = project.findProperty("antlr.version")
    antlr("org.antlr:antlr4:${version}")
    api("org.antlr:antlr4-runtime:${version}")
}

sourceSets {
    main {
        antlr {
            setSrcDirs(listOf("../../grammar"))
        }
        java {
            srcDir("build/generated/sources/antlr/main/java")
        }
    }
}

tasks.generateGrammarSource {
    val buildDir = layout.buildDirectory.get().toString()
    outputDirectory = file("${buildDir}/generated/sources/antlr/main/java/org/cqframework/cql/gen")
    arguments = listOf("-visitor", "-package", "org.cqframework.cql.gen")
}

tasks.compileKotlin {
    dependsOn(tasks.generateGrammarSource)
}

tasks.named("dokkaJavadocJar") {
    dependsOn(tasks.generateGrammarSource)
}