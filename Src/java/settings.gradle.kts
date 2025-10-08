pluginManagement {
    plugins {
        kotlin("jvm") version "2.2.10"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.8.0")
}

rootProject.name = "cql-all"

include(
    "cqf-fhir",
    "cqf-fhir-npm",
    "cql",
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
