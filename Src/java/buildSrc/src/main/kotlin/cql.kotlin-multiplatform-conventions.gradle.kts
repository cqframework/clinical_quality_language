import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("cql.maven-publishing-conventions")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.dokka")
    id("com.github.gmazzo.buildconfig")
    kotlin("plugin.serialization")
}


repositories {
    mavenCentral()
    maven {
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
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
        "src/jsTest/kotlin"
    )

    // Custom config with overrides.
    config.setFrom("$rootDir/config/detekt/detekt.yml")
}

kotlin {
    compilerOptions {
        // Expect/Actual classes are currently in Beta
        // This suppresses warning about that for now.
        // Assuming expect/actual classes are removed,
        // we'll need to refactor the code to use interfaces.
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    jvmToolchain(17)
    jvm()

    js {
        useEsModules()
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        nodejs()
        binaries.library()
        generateTypeScriptDefinitions()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
        binaries.library()
        generateTypeScriptDefinitions()
    }

    sourceSets {
        commonMain {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-io-core:0.8.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-io:1.9.0")
                implementation("io.github.oshai:kotlin-logging:7.0.3")
            }
        }

        jsMain {
            dependencies {
            }
        }

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
                implementation("org.hamcrest:hamcrest-all:1.3")
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

// JAR manifests aren't available in Kotlin/JS, so to access Package.implementationVersion, a build config is needed.
buildConfig {
    buildConfigField("IMPLEMENTATION_VERSION", project.version.toString())
}
