import gradle.kotlin.dsl.accessors._2d946b1c62c86c83708f6e287986eee0.implementation
import gradle.kotlin.dsl.accessors._2d946b1c62c86c83708f6e287986eee0.spotless

plugins {
    kotlin("jvm")
    id("com.diffplug.spotless")
    id("io.gitlab.arturbosch.detekt")
}

spotless {
    kotlin {
        ktfmt().kotlinlangStyle()
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

