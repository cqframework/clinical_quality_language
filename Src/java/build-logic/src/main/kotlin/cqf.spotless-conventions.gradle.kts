// Spotless code-formatting conventions, pinned and shared across the division.
//
// Apply once at the consumer's root project:
//
//   plugins {
//       id("cqf.spotless-conventions")
//   }
//
// The plugin targets all `**/*.java` and `**/*.kt` files under the root directory, including
// `build-logic/**`, so it formats the entire repository — including the precompiled-script
// plugins that drive the build itself. Do NOT also apply this plugin per-subproject; the root
// application already covers them via the rootDir-rooted globs.
//
// Pinned tool versions (kept in lockstep across consumers):
//   - spotless 8.4.0           (declared in the consumer's build-logic/build.gradle.kts)
//   - palantirJavaFormat 2.89.0
//   - ktfmt 0.56 + kotlinlangStyle()
//
// Canonical source: home/shared/gradle/conventions/cqf.spotless-conventions.gradle.kts.

plugins { id("com.diffplug.spotless") }

spotless {
    java {
        target("**/*.java")
        targetExclude(
            "**/generated/**",
            "**/generated-sources/**",
            "**/build/**",
            "**/bin/**",
            "**/.gradle/**",
        )
        palantirJavaFormat("2.89.0")
    }
    kotlin {
        target("**/*.kt")
        targetExclude(
            "**/generated/**",
            "**/generated-sources/**",
            "**/build/**",
            "**/bin/**",
            "**/.gradle/**",
        )
        ktfmt("0.56").kotlinlangStyle()
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        targetExclude(
            "**/generated/**",
            "**/generated-sources/**",
            "**/build/**",
            "**/bin/**",
            "**/.gradle/**",
        )
        ktfmt("0.56").kotlinlangStyle()
    }
}
