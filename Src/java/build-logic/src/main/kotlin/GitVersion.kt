import java.io.File

/**
 * Resolves the project version from the current git state.
 *
 * The resolution rules, in order:
 * 1. **Release** — if HEAD is tagged with `vX.Y.Z[...]`, use the tag with the `v` prefix stripped
 *    (e.g. `v4.6.0` → `4.6.0`). Produces a non-SNAPSHOT version, which triggers artifact signing in
 *    `cql.maven-publishing-conventions`.
 * 2. **main SNAPSHOT** — if on the `main` branch with no tag at HEAD, take the most recent `vX.Y.Z`
 *    tag reachable from HEAD, bump the minor component, reset patch to 0, and append `-SNAPSHOT`
 *    (e.g. last tag `v4.6.0` → `4.7.0-SNAPSHOT`).
 * 3. **Branch SNAPSHOT** — otherwise, append a branch identifier and the short SHA to the bumped
 *    base (e.g. `4.7.0-feature-xyz-5ac419a5d-SNAPSHOT`). Branch name is taken from
 *    `GITHUB_REF_NAME` if set (the GitHub Actions convention), else from `git rev-parse`; a
 *    detached HEAD falls back to the literal `detached`.
 *
 * Fallbacks keep the build working in edge cases: no tags anywhere → base `0.0.0` (bumps to
 * `0.1.0`); `git` invocation failure → empty string, which the callers treat as "no data."
 *
 * The `-SNAPSHOT` suffix on non-release builds is load-bearing: the maven-publish convention checks
 * `version.endsWith("SNAPSHOT")` to decide whether to sign and which repository to target.
 */
fun gitVersion(rootDir: File): String {
    val exactTag =
        runGit(rootDir, "tag", "--points-at", "HEAD")
            .lineSequence()
            .map { it.trim() }
            .firstOrNull { it.matches(Regex("v\\d+\\.\\d+\\.\\d+.*")) }
    if (exactTag != null) {
        return exactTag.removePrefix("v")
    }

    val lastTag =
        runGit(rootDir, "describe", "--tags", "--abbrev=0", "--match=v*.*.*").trim().takeIf {
            it.isNotEmpty()
        }
    val baseVersion = lastTag?.removePrefix("v") ?: "0.0.0"
    val (major, minor, _) = parseVersion(baseVersion)
    val bumped = "$major.${minor + 1}.0"

    val branch = detectBranch(rootDir)
    if (branch == "main") {
        return "$bumped-SNAPSHOT"
    }

    val shortSha = runGit(rootDir, "rev-parse", "--short", "HEAD").trim().ifEmpty { "nosha" }
    val sanitized = sanitizeBranch(branch)
    return "$bumped-$sanitized-$shortSha-SNAPSHOT"
}

/** Parses `X.Y.Z` out of a version string, ignoring any `-suffix`. Missing parts default to 0. */
private fun parseVersion(v: String): Triple<Int, Int, Int> {
    val core = v.split("-", limit = 2)[0].split(".")
    return Triple(
        core.getOrNull(0)?.toIntOrNull() ?: 0,
        core.getOrNull(1)?.toIntOrNull() ?: 0,
        core.getOrNull(2)?.toIntOrNull() ?: 0,
    )
}

/**
 * Picks the branch name. `GITHUB_REF_NAME` is preferred because GitHub Actions often checks out in
 * detached-HEAD mode where `git rev-parse --abbrev-ref HEAD` only yields `HEAD`.
 */
private fun detectBranch(rootDir: File): String {
    System.getenv("GITHUB_REF_NAME")
        ?.takeIf { it.isNotBlank() }
        ?.let {
            return it
        }
    val branch = runGit(rootDir, "rev-parse", "--abbrev-ref", "HEAD").trim()
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

/** Runs `git <args>` in [workingDir]. Returns stdout on success, empty string on any failure. */
private fun runGit(workingDir: File, vararg args: String): String {
    return try {
        val proc =
            ProcessBuilder(listOf("git") + args.toList())
                .directory(workingDir)
                .redirectErrorStream(false)
                .start()
        val out = proc.inputStream.bufferedReader().readText()
        proc.errorStream.bufferedReader().readText()
        if (proc.waitFor() == 0) out else ""
    } catch (_: Exception) {
        ""
    }
}
