import java.io.File
import org.gradle.api.provider.ProviderFactory
import org.gradle.process.ExecOutput

/**
 * Resolves the project version from the current git state, parameterized by a [VersionScheme] so
 * the same logic works for semver and calendar-style repos.
 *
 * Canonical source: `home/shared/gradle/conventions/GitVersion.kt`. Consumers vendor a copy into
 * their own `build-logic/`; propagate enhancements through `shared/` so all consumers stay aligned.
 *
 * The resolution rules, in order:
 * 1. **Release** — if HEAD is tagged with a `v<version>` where `<version>` matches
 *    [VersionScheme.tagRegex], use the tag with the `v` prefix stripped (e.g. `v2026.03.R01` →
 *    `2026.03.R01`). Produces a non-SNAPSHOT version.
 * 2. **main SNAPSHOT** — if on the `main` branch with no release tag at HEAD, take the most recent
 *    tag reachable from HEAD matching [VersionScheme.describeGlob], feed it through
 *    [VersionScheme.bumpForSnapshot], and append `-SNAPSHOT`.
 * 3. **Branch SNAPSHOT** — otherwise, append a sanitized branch identifier and the short SHA to the
 *    bumped base (e.g. `2026.03.R02-feature-xyz-5ac419a5d-SNAPSHOT`). Branch name is taken from
 *    `GITHUB_REF_NAME` / `CI_COMMIT_REF_NAME` if set (CI convention), else from `git rev-parse`; a
 *    detached HEAD falls back to the literal `detached`.
 *
 * Fallbacks keep the build working in edge cases: no tags anywhere → [VersionScheme.fallbackBase];
 * `git` invocation failure → empty string, which callers treat as "no data."
 *
 * The `-SNAPSHOT` suffix on non-release builds is load-bearing: downstream consumers (e.g. Maven
 * publishing) key off `version.endsWith("SNAPSHOT")`.
 *
 * Uses [ProviderFactory.exec] rather than `ProcessBuilder` so the invocation is compatible with
 * Gradle's configuration cache (which forbids arbitrary external processes at configuration time).
 */
fun gitVersion(rootDir: File, scheme: VersionScheme, providers: ProviderFactory): String {
    fun git(vararg args: String): String = runGit(rootDir, providers, *args)

    val exactTag =
        git("tag", "--points-at", "HEAD")
            .lineSequence()
            .map { it.trim() }
            .firstOrNull { it.startsWith("v") && scheme.tagRegex.matches(it.removePrefix("v")) }
    if (exactTag != null) {
        return exactTag.removePrefix("v")
    }

    val lastTag =
        git("describe", "--tags", "--abbrev=0", "--match=${scheme.describeGlob}").trim().takeIf {
            it.isNotEmpty()
        }
    val baseVersion = lastTag?.removePrefix("v") ?: scheme.fallbackBase
    val bumped = scheme.bumpForSnapshot(baseVersion)

    val branch = detectBranch(rootDir, providers)
    if (branch == "main") {
        return "$bumped-SNAPSHOT"
    }

    val shortSha = git("rev-parse", "--short", "HEAD").trim().ifEmpty { "nosha" }
    val sanitized = sanitizeBranch(branch)
    return "$bumped-$sanitized-$shortSha-SNAPSHOT"
}

/**
 * Picks the branch name. CI-provided env vars are preferred because CI runners often check out in
 * detached-HEAD mode where `git rev-parse --abbrev-ref HEAD` only yields `HEAD`. Both GitHub
 * Actions (`GITHUB_REF_NAME`) and GitLab CI (`CI_COMMIT_REF_NAME`) are supported.
 */
private fun detectBranch(rootDir: File, providers: ProviderFactory): String {
    sequenceOf("GITHUB_REF_NAME", "CI_COMMIT_REF_NAME")
        .mapNotNull { System.getenv(it) }
        .firstOrNull { it.isNotBlank() }
        ?.let {
            return it
        }
    val branch = runGit(rootDir, providers, "rev-parse", "--abbrev-ref", "HEAD").trim()
    return if (branch.isBlank() || branch == "HEAD") "detached" else branch
}

/**
 * Makes a branch name safe to embed in a Maven version string: replaces runs of non-alphanumerics
 * with `-`, trims leading/trailing separators, and caps length at 40 chars.
 */
private fun sanitizeBranch(branch: String): String {
    val cleaned = branch.replace(Regex("[^A-Za-z0-9]+"), "-").trim('-')
    return cleaned.ifEmpty { "branch" }.take(40).trimEnd('-')
}

/**
 * Runs `git <args>` in [workingDir] via Gradle's config-cache-safe exec API. Returns stdout on
 * success, empty string on any non-zero exit or unexpected failure. `isIgnoreExitValue = true`
 * keeps a failed invocation (e.g. `git describe` with no tags) from aborting the build.
 */
private fun runGit(workingDir: File, providers: ProviderFactory, vararg args: String): String {
    val exec: ExecOutput =
        providers.exec {
            workingDir(workingDir)
            commandLine(listOf("git") + args.toList())
            isIgnoreExitValue = true
        }
    return if (exec.result.get().exitValue == 0) exec.standardOutput.asText.get() else ""
}
