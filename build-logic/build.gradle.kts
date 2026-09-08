plugins { `kotlin-dsl` }

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.antlr.kotlin.gradle.plugin)
    implementation(libs.xsom)
    implementation(libs.kotlinpoet)
}

kotlin { jvmToolchain(17) }
