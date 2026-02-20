pluginManagement {
    includeBuild("build-logic")
    plugins {
        kotlin("jvm") version "2.3.10"
        id("com.vanniktech.maven.publish") version "0.36.0"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("1.0.0")
}

rootProject.name = "cql-all"

include(
    "cql-bom",
    "cqf-fhir",
    "cqf-fhir-npm",
    "cql",
    "docs",
    "elm",
    "shared",
    "engine",
    "engine-fhir",
    "qdm",
    "quick",
    "cql-to-elm",
    "cql-to-elm-cli",
    "elm-fhir",
    "ucum",
    "tools:rewrite",
    "tools:cql-formatter",
    "tools:cql-parsetree",
    "tools:xsd-to-modelinfo"
)
