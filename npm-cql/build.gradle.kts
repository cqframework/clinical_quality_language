plugins { id("cql.node-gradle-conventions") }

val engineJsDistDir = project(":engine").layout.buildDirectory.dir("dist/js/productionLibrary")
val preparedPackageDir = layout.buildDirectory.dir("npm-package")

/** Assembles the `@cqframework/cql` NPM package. */
val preparePackage by
    tasks.registering(Sync::class) {
        dependsOn(":engine:jsNodeProductionLibraryDistribution")

        from(engineJsDistDir) { exclude("package.json") }
        from(layout.projectDirectory) {
            include("package.json", "README.md")
            expand("version" to project.version.toString())
        }
        into(preparedPackageDir)
    }

tasks.named("build") { dependsOn(preparePackage) }
