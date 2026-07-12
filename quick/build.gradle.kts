plugins {
    id("cql.library-conventions")
    id("cql.xjc-conventions")
}

dependencies { implementation(project(":cql-to-elm")) }

val generateQuick =
    tasks.register<XjcTask>("generateQuick") {
        schema.set("${projectDir}/schema/v1.4/quick.xsd")
        binding.set("${projectDir}/schema/v1.4/quick-binding.xjb")
    }

sourceSets { main { java { srcDir(generateQuick.map { it.outputDir }) } } }
