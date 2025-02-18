pluginManagement {
    plugins {
        kotlin("jvm") version "2.0.20"
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
    "model",
    "model-xmlutil",
    "elm",
    "elm-xmlutil",
    "elm-test",
    "engine",
    "engine-fhir",
    "qdm",
    "quick",
    "cql-to-elm",
    "cql-to-elm-cli",
    "elm-fhir",
    "ucum",
    "tools:cql-formatter",
    "tools:cql-parsetree",
    "tools:xsd-to-modelinfo"
)
