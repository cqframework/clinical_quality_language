plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("1.0.0")

    // Makes version catalog accessors available in conventions plugins
    id("dev.panuszewski.typesafe-conventions") version "0.11.1"
}

rootProject.name = "build-logic"
