plugins {
    kotlin("jvm")
    id("com.diffplug.spotless")
    id("io.gitlab.arturbosch.detekt")
}

repositories {
    mavenCentral()
}

spotless {
    kotlin {
        ktfmt().kotlinlangStyle()
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

