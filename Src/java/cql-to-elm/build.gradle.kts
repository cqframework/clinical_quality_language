import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("cql.kotlin-multiplatform-conventions")
}

val loadTestResourcesTask = tasks.register<LoadTestResourcesTask>("loadTestResources")

kotlin {
    js {
        outputModuleName = "cql-to-elm"
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "cql-to-elm"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":cql"))
                api(project(":elm"))
            }
        }

        // Add source sets with TestResource implementations
        matching { it.name.endsWith("Test") }.configureEach {
            kotlin.srcDir(loadTestResourcesTask.get().getDirForSourceSet(name))
        }

        jvmTest {
            dependencies {
                implementation(project(":quick"))
                implementation(project(":qdm"))
                implementation(project(":ucum"))
                implementation("com.tngtech.archunit:archunit:1.2.1")
            }
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    dependsOn(loadTestResourcesTask)
}
