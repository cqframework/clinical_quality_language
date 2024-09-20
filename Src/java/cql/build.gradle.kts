import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask

plugins {
    id("cql.java-conventions")
    id("cql.kotlin-conventions")
    id("application")
    id("com.strumenta.antlr-kotlin") version "1.0.0"
}

dependencies {
    api("com.strumenta:antlr-kotlin-runtime:1.0.0")
}

application {
    mainClass = "org.cqframework.cql.Application"
}

sourceSets {
    main {
        kotlin {
            srcDir("build/generated/sources/antlr/main/kotlin")
        }
    }
}

val generateKotlinGrammarSource = tasks.register<AntlrKotlinTask>("generateKotlinGrammarSource") {
    dependsOn("cleanGenerateKotlinGrammarSource")

    source = fileTree("../../grammar") {
        include("**/*.g4")
    }

    // We want the generated source files to have this package name
    packageName = "org.cqframework.cql.gen"

    // We want visitors alongside listeners.
    // The Kotlin target language is implicit, as is the file encoding (UTF-8)
    arguments = listOf("-visitor")

    // Generated files are outputted inside build/generatedAntlr/{package-name}
    outputDirectory = file("build/generated/sources/antlr/main/kotlin/${packageName!!.replace(".", "/")}")
}

tasks.sourcesJar {
    from(generateKotlinGrammarSource)
}

tasks.compileKotlin {
    dependsOn(generateKotlinGrammarSource)
}