plugins {
    kotlin("multiplatform")
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

kotlin {
    jvmToolchain(11)
    jvm {
        withJava()
    }

    // This adds JavaScript as build target.
    // Running the build outputs packages in the build/js/packages directory.
    // These packages can e.g. be required or imported in JS or TS projects.
    // If you get `Task :kotlinStoreYarnLock FAILED` during the build,
    // run the `kotlinUpgradeYarnLock` task and build again.
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

    sourceSets {
        commonMain{
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        jvmMain {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        jsMain {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }

        jsTest {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}