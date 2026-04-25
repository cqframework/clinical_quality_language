// Computes the project version from git state and applies it to the root project and all
// subprojects. Resolved once at root configuration time to avoid re-invoking git per subproject.
// See GitVersion.kt and VersionScheme.kt for the resolution rules.
//
// Scheme selection: defaults to SemverScheme (`X.Y.Z`). Set the project property
// `cqf.gitVersion.scheme=calendar` (e.g. in gradle.properties) to use CalendarReleaseScheme
// (`YYYY.MM.RNN`).
//
// Canonical source: `home/shared/gradle/conventions/cqf.git-version.gradle.kts`.

val schemeName = providers.gradleProperty("cqf.gitVersion.scheme").orElse("semver").get()
val scheme: VersionScheme =
    when (schemeName.lowercase()) {
        "semver" -> SemverScheme
        "calendar" -> CalendarReleaseScheme
        else ->
            error(
                "Unknown cqf.gitVersion.scheme: '$schemeName' (expected 'semver' or 'calendar')"
            )
    }
val resolvedVersion = gitVersion(rootDir, scheme, providers)

allprojects { version = resolvedVersion }

logger.lifecycle("Resolved project version: $resolvedVersion")
