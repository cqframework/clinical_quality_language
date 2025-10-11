# Repository Guidelines

## Project Structure & Module Organization
This Gradle multi-module workspace centralizes Kotlin libraries for translating and executing CQL. Core generators live in `cql-to-elm`, with domain model for cql and elm under `cql` and `elm` respectively ; runtime logic is under `engine` and `engine-fhir`; FHIR and model packages sit in `cqf-fhir`, `cqf-fhir-npm`, `quick`, `qdm`, and `ucum`. Command-line utilities reside in `cql-to-elm-cli` and `tools/*`. Source code follows `src/commonMain/kotlin` for multi-platform modules or `src/main/kotlin` for JVM-only modules; tests mirror that in `src/test/...`. Prefer multi-platform where possible. Generated artifacts land under each module's `build/` directory—do not commit them.

## Build, Test, and Development Commands
- `./gradlew build` compiles all modules, runs unit tests, and assembles publishable artifacts.
- `./gradlew check` runs the aggregated verification suite, including Detekt and formatting checks when configured.
- `./gradlew :cql-to-elm-cli:run --args "--input path/to/file.cql"` executes the translator CLI directly.
- `./gradlew installDist` produces runnable scripts in each application module's `build/install/` directory.

## Coding Style & Naming Conventions
Spotless applies ktfmt; run `./gradlew spotlessApply` before sending changes. Kotlin code use four-space indentation and stay within the `org.cqframework` package structure for the compiler, and `org.opencds.cqf` for the engine. Adhere to module-specific namespaces (for example, `org.cqframework.cql.cql2elm` for translator code). Keep test classes suffixed with `Test` or `IT` as appropriate. Static analysis uses Detekt with the shared profile in `config/detekt/detekt.yml`; address violations or document temporary suppressions inline.

## Testing Guidelines
Unit tests are written with JUnit 5 and Mockito; place them under `src/commonTest/kotlin` or `src/test/kotlin`. Run `./gradlew test` for the full suite or scope to a module via `./gradlew :engine:test`. Generate coverage with `./gradlew jacocoTestReport` and ensure new features keep coverage stable. When adding models or sample artifacts, update corresponding fixtures module-specific `src/test/resources` folders.

## Commit & Pull Request Guidelines
Recent history favors short, imperative commit subjects (for example, `Handle missing measurement period`). Avoid leaving `WIP` in final commits. Group related changes per module and mention impacted packages in body text. Pull requests should explain the business problem, cite related issues, outline testing performed (commands and modules), and attach CLI output or screenshots for user-facing tools. Request reviews from module owners and verify CI status before merging.
