pluginManagement {
    plugins {
        kotlin("jvm") version "2.0.0"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.5.0")
}

rootProject.name = "cql-all"

include(
    "cqf-fhir",
    "cqf-fhir-npm",
    "cql",
    "model",
    "model-jackson",
    "model-jaxb",
    "elm",
    "elm-jackson",
    "elm-jaxb",
    "elm-test",
    "engine",
    "engine-fhir",
    "qdm",
    "quick",
    "cql-to-elm",
    "cql-to-elm-cli",
    "elm-fhir",
    "tools:cql-formatter",
    "tools:cql-parsetree",
    "tools:xsd-to-modelinfo"
)

