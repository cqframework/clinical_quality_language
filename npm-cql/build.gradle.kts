plugins { id("cql.node-gradle-conventions") }

val engineJsDistDir = project(":engine").layout.buildDirectory.dir("dist/js/productionLibrary")

val generateTypeDefinitions by
    tasks.registering(GenerateTypeDefinitions::class) {
        description = "Generates .d.mts files for the NPM package."

        dependsOn(
            ":engine:jsBrowserProductionLibraryDistribution",
            ":engine:jsNodeProductionLibraryDistribution",
        )

        inputDir.set(engineJsDistDir)
        inputDMts.set("engine")
        outputDir.set(layout.buildDirectory.dir("generated-type-definitions"))
        modules.set(listOf("cql", "cql-to-elm", "elm", "engine", "kotlin-kotlin-stdlib", "shared"))
    }

val preparedPackageDir = layout.buildDirectory.dir("npm-package")

val preparePackage by
    tasks.registering(Sync::class) {
        description = "Assembles the `@cqframework/cql` NPM package."

        dependsOn(generateTypeDefinitions)

        from(engineJsDistDir) { exclude("package.json") }
        from(layout.projectDirectory) {
            include("package.json", "README.md")
            expand("version" to project.version.toString())
        }
        from(generateTypeDefinitions.map { it.outputDir })
        into(preparedPackageDir)
    }

tasks.named("build") { dependsOn(preparePackage) }
