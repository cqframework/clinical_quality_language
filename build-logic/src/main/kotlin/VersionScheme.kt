/**
 * Strategy for parsing release tags and computing the next SNAPSHOT base version.
 *
 * Canonical source: `home/shared/gradle/conventions/VersionScheme.kt`. Consumers vendor a copy into
 * their own `build-logic/`; propagate enhancements through `shared/`.
 *
 * [gitVersion] is scheme-agnostic; all format-specific behavior lives here so the same resolver can
 * be shared across repos with different versioning conventions. Two built-in schemes are provided:
 * [SemverScheme] for `X.Y.Z` (bumps minor) and [CalendarReleaseScheme] for `YYYY.MM.RNN` (bumps the
 * `R` counter).
 */
interface VersionScheme {
    /**
     * Regex matched against a candidate version string (already stripped of its leading `v`) to
     * decide whether a tag on HEAD is a release tag for this scheme. Used only for the
     * exact-tag-at-HEAD check.
     */
    val tagRegex: Regex

    /**
     * Glob passed to `git describe --tags --match=<glob>` when searching for the most recent tag
     * reachable from HEAD. Must match the full tag name including the `v` prefix.
     */
    val describeGlob: String

    /**
     * Base version to bump from when there are no tags at all in the repo. Must itself parse
     * cleanly via [bumpForSnapshot].
     */
    val fallbackBase: String

    /**
     * Given a baseline release version (already stripped of its leading `v` and any `-suffix`),
     * returns the next SNAPSHOT base — i.e. what the in-progress version between this release and
     * the next should look like, minus the `-SNAPSHOT` suffix.
     */
    fun bumpForSnapshot(baseVersion: String): String
}

/** Semantic versioning (`X.Y.Z`). Bump rule: increment minor, reset patch. */
object SemverScheme : VersionScheme {
    override val tagRegex = Regex("""\d+\.\d+\.\d+.*""")
    override val describeGlob = "v*.*.*"
    override val fallbackBase = "0.0.0"

    override fun bumpForSnapshot(baseVersion: String): String {
        val core = baseVersion.split("-", limit = 2)[0].split(".")
        val major = core.getOrNull(0)?.toIntOrNull() ?: 0
        val minor = core.getOrNull(1)?.toIntOrNull() ?: 0
        return "$major.${minor + 1}.0"
    }
}

/**
 * Calendar-based release versioning (`YYYY.MM.RNN`, e.g. `2026.03.R01`). Bump rule: increment the
 * `R` counter; `YYYY.MM` is only advanced when a new release is tagged in a new month. That means
 * the SNAPSHOT between `v2026.03.R01` and a future April cut is `2026.03.R02-SNAPSHOT` until the
 * April tag lands — deterministic and independent of the wall clock.
 */
object CalendarReleaseScheme : VersionScheme {
    private val parse = Regex("""(\d{4})\.(\d{2})\.R(\d+)(?:-.*)?""")

    override val tagRegex = Regex("""\d{4}\.\d{2}\.R\d+.*""")
    override val describeGlob = "v*.*.R*"
    override val fallbackBase = "0000.00.R00"

    override fun bumpForSnapshot(baseVersion: String): String {
        val m = parse.matchEntire(baseVersion) ?: return "0000.00.R01"
        val (yyyy, mm, rn) = m.destructured
        val width = rn.length.coerceAtLeast(2)
        return "$yyyy.$mm.R%0${width}d".format(rn.toInt() + 1)
    }
}
