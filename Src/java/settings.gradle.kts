pluginManagement {
    plugins {
        kotlin("multiplatform") version "1.9.25"
        kotlin("jvm") version "1.9.25"
    }
}

rootProject.name = "cql-all"

include(
    "cql",
)


