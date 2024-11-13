import net.ltgt.gradle.errorprone.errorprone

plugins {
    id("java")
    id("net.ltgt.errorprone")
    id("checkstyle")
}

repositories {
    mavenCentral()
}
dependencies {
    errorprone("com.google.errorprone:error_prone_core:2.29.2")
}

tasks.named<Checkstyle>("checkstyleMain") {
    exclude { it.file.path.contains("generated")}
}

tasks.named<Checkstyle>("checkstyleTest") {
    enabled = false
}

tasks.withType<JavaCompile>().configureEach {
    options.errorprone.disableAllWarnings = true
    options.errorprone.disableWarningsInGeneratedCode = true
    options.errorprone.disable("DoubleBraceInitialization")
    // errorproneArgs = ["-XepPatchLocation:IN_PLACE"]
}