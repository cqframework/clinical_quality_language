plugins {
    id("cql.library-conventions")
    id("cql.xjc-conventions")
}

dependencies {
    implementation(project(":cql-to-elm"))

    xjcRuntimeDeps.forEach { api(it) }
}

val generateQuick =
    tasks.register<XjcTask>("generateQuick") {
        outputDir.set(project.layout.buildDirectory.dir("generated/sources/quick/main/java"))
        schema.set("${projectDir}/schema/v1.4/quick.xsd")
        binding.set("${projectDir}/schema/v1.4/quick-binding.xjb")
    }

sourceSets { main { java { srcDir(generateQuick.map { it.outputDir }) } } }
