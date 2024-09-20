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
    js(IR) {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }

        binaries.executable()
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