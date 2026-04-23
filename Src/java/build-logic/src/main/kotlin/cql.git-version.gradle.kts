// Computes the project version from git state and applies it to the root project and all
// subprojects. Resolved once at configuration time of the root project to avoid re-invoking git
// per subproject. See GitVersion.kt for the resolution rules and the rationale behind them.

val resolvedVersion = gitVersion(rootDir)

allprojects {
    version = resolvedVersion
}

logger.lifecycle("Resolved project version: $resolvedVersion")
