import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile

plugins {
    id("cql.kotlin-multiplatform-conventions")
}

val buildDir = project.layout.buildDirectory.get().toString()
val destDir = "${buildDir}/generated/sources/$name/commonMain/kotlin"

kotlin {
    sourceSets {
        commonMain {
            kotlin {
                srcDir(destDir)
            }
        }
    }
}

val xsdKotlinGenTask = tasks.register<XsdKotlinGenTask>("runXsdKotlinGen")

tasks.withType<XsdKotlinGenTask>().configureEach {
    mustRunAfter(tasks.withType<Delete>())
}

tasks.withType<AbstractKotlinCompile<*>>().configureEach {
    dependsOn(xsdKotlinGenTask)
}

tasks.named("clean").configure {
    doLast {
        delete(destDir)
    }
}