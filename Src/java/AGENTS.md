# Repository Guidelines

## Project Structure & Module Organization
This workspace is a Gradle multi-module build. Library code resides under directories like `cql`, `elm`, `shared`, `engine`, and `engine-fhir`, while CLI tooling lives in `cql-to-elm-cli` and `tools/*`. Domain resources sit in `qdm`, `quick`, and `ucum`. Shared Gradle logic is centralized in `buildSrc`. Each module follows the `src/main/kotlin` and `src/test/kotlin` layout; data fixtures go in `src/test/resources`.

## Build, Test, and Development Commands
Use JDK 17. Run `./gradlew build` for a full compile and test cycle across all modules. `./gradlew check` adds static analysis and documentation validation. Use module-scoped commands such as `./gradlew :engine:test` for focused feedback. Examples of runnable tooling include `./gradlew :cql-to-elm-cli:run` and `./gradlew :tools:cql-parsetree:run`. Clean artifacts with `./gradlew clean`, and refresh distributions via `./gradlew installDist`.

## Coding Style & Naming Conventions
Kotlin sources follow the Detekt ruleset in `config/detekt/detekt.yml`, applied by the `cql.kotlin-conventions` plugin. Keep four-space indentation, `UpperCamelCase` for types, `lowerCamelCase` for functions and properties, and suffix implementation-specific classes with the domain (for example `ElmModelResolver`). Prefer standard Kotlin null-safety patterns over platform types. Run `./gradlew detekt` before opening a review.

## Testing Guidelines
Unit and integration tests use JUnit Jupiter with Kotlin test assertions; name files `*Test.kt` and methods with descriptive phrases (`fun evaluatesNullCoalesce()` is preferred). Execute `./gradlew test` or `./gradlew :shared:test` locally, and include relevant fixtures in `src/test/resources`. JaCoCo reports (`./gradlew jacocoTestReport`) should remain green; flag any coverage drops in your PR.

## Commit & Pull Request Guidelines
Write imperative, concise commit subjects mirroring the repository’s history (e.g., `Fix playground deployment action (#1628)`). Reference tracked issues or PR numbers in parentheses. For pull requests, provide a problem statement, the solution outline, verification steps (commands or screenshots where applicable), and note any follow-up tasks. Ensure CI is green before requesting review.
