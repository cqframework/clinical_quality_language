import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("cql.maven-publishing-conventions")
    alias(libs.plugins.kover)
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.kotlinx.serialization)
}

repositories {
    mavenCentral()
    maven { url = uri("https://central.sonatype.com/repository/maven-snapshots/") }
}

detekt {
    // Applies the config files on top of detekt's default config.
    buildUponDefaultConfig = true

    // The directories where detekt looks for source files.
    source.setFrom(
        "src/commonMain/kotlin",
        "src/jvmMain/kotlin",
        "src/jsMain/kotlin",
        "src/commonTest/kotlin",
        "src/jvmTest/kotlin",
        "src/jsTest/kotlin",
    )

    // Custom config with overrides.
    config.setFrom("$rootDir/config/detekt/detekt.yml")
}

// Used to skip JS/WASM build and publishing if the project doesn't have any common/JS/WASM sources
val enableJsTargets =
    listOf("common", "js", "wasmJs").any {
        layout.projectDirectory.dir("src/${it}Main").asFile.exists()
    }

kotlin {
    compilerOptions {
        // Expect/Actual classes are currently in Beta
        // This suppresses warning about that for now.
        // Assuming expect/actual classes are removed,
        // we'll need to refactor the code to use interfaces.
        freeCompilerArgs.add("-Xexpect-actual-classes")
        freeCompilerArgs.add("-Xwarning-level=DEPRECATION:disabled")

        optIn.add("kotlin.js.ExperimentalJsExport")
        optIn.add("kotlin.js.ExperimentalJsStatic")
    }
    jvmToolchain(17)
    jvm()

    if (enableJsTargets) {
        js {
            compilerOptions {
                // Enable support for BigInt
                freeCompilerArgs.add("-Xes-long-as-bigint")
            }
            useEsModules()
            browser { testTask { enabled = false } }
            nodejs { testTask { useMocha { timeout = "30s" } } }
            binaries.library()
            generateTypeScriptDefinitions()
        }

        @OptIn(ExperimentalWasmDsl::class)
        wasmJs {
            compilerOptions { optIn.add("kotlin.js.ExperimentalWasmJsInterop") }
            browser { testTask { enabled = false } }
            nodejs { testTask { enabled = false } }
            binaries.library()
            generateTypeScriptDefinitions()
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlinx.io.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.serialization.json.io)
                implementation(libs.kotlin.logging)
            }
        }

        jvmMain { dependencies { api(libs.kotlinx.io.core.jvm) } }

        commonTest { dependencies { implementation(libs.kotlin.test) } }

        jvmTest {
            dependencies {
                implementation(libs.kotlin.test.junit5)
                implementation(libs.junit.jupiter)
                implementation(libs.slf4j.simple)
                implementation(libs.hamcrest.all)
            }
        }
    }
}

tasks.withType<Test> { useJUnitPlatform() }

tasks.register<Jar>("dokkaHtmlJar") {
    dependsOn(tasks.named("dokkaGeneratePublicationHtml"))
    from(tasks.named("dokkaGeneratePublicationHtml").map { it.outputs.files })
    archiveClassifier.set("html-docs")
}

// Dokka exposes consumable configurations with attributes that loosely match java-runtime,
// which causes Gradle to select them instead of the actual JVM runtime variant in composite
// builds (KT-52172 workaround). Mark them as non-consumable.
afterEvaluate {
    configurations
        .matching { it.name.startsWith("dokka") && it.isCanBeConsumed }
        .configureEach { isCanBeConsumed = false }
}

// JAR manifests aren't available in Kotlin/JS, so to access Package.implementationVersion, a build
// config is needed.
buildConfig { buildConfigField("IMPLEMENTATION_VERSION", project.version.toString()) }

kover { reports { total { xml { onCheck = true } } } }
