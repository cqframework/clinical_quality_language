import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("com.diffplug.spotless")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.dokka")
}

repositories {
    mavenCentral()
}

spotless {
    kotlin {
        ktfmt().kotlinlangStyle()
    }
}

kotlin {
    jvmToolchain(17)
    jvm {
        withJava()
    }

    // This adds JavaScript as build target.
    // Running the build outputs packages in the build/js/packages directory.
    // These packages can e.g. be required or imported in JS or TS projects.
    // If you get `Task :kotlinStoreYarnLock FAILED` during the build,
    // run the `:kotlinUpgradeYarnLock` task and build again.
    // Run `jsRun --continuous` to start a local development server with
    // live reloading (automatic re-build on file changes). The local server serves
    // <module>/src/jsMain/resources/index.html from the root.
    js(IR) {

        // Output ES2015 modules (.mjs files) to build/js/packages/<package>/kotlin
        // instead of default UMD (.js) modules.
        useEsModules()

        // Set web browser environment as the target execution environment.
        // This also runs webpack which bundles everything into a single
        // <module>/build/dist/js/productionExecutable/<module>.js file.
        // This file can be e.g. included in an HTML file and distributed
        // via a CDN.
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }

        // `nodejs {}` can be added here to set Node.js as the target execution
        // environment. If no browser APIs are used, having just `browser {}`
        // creates an isomorphic library.

        // Explicitly instruct the Kotlin compiler to emit executable JS code.
        // If `binaries.library()` is used instead, the
        // <module>/build/dist/js/productionExecutable directory has
        // un-webpacked JS.
        binaries.executable()

        // Generate TypeScript definitions (.d.ts files) from Kotlin code. The files are
        // finally saved to build/js/packages/<package>/kotlin.
        generateTypeScriptDefinitions()
    }

    // Add Kotlin/WASM compilation target.
    // The output is in the JS packages directory.
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser { }
        binaries.executable()
        generateTypeScriptDefinitions()
    }

    sourceSets {
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test-junit5"))
                implementation("org.junit.jupiter:junit-jupiter")
                implementation("org.slf4j:slf4j-simple:2.0.13")
            }
        }

        jsTest {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register<Jar>("dokkaHtmlJar") {
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("html-docs")
}

tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}