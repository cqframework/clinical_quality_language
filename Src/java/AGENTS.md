# Repository Guidelines

## Project Structure & Module Organization
- Gradle multiplatform workspace with library code under `cql`, `elm`, `shared`, `engine`, and `engine-fhir`.
- CLI tooling lives in `cql-to-elm-cli`; supplemental tools are in `tools/*`.
- Domain model packages such as `qdm`, `quick`, and `ucum` provide shared resources.
- Each module follows `src/main/kotlin` for sources and `src/test/kotlin` plus `src/test/resources` for tests and fixtures.
- Shared build logic resides in `buildSrc`; grammar assets for the CQL parser are under `grammar/`.

## Build, Test, and Development Commands
- `./gradlew build` – compile all modules and execute unit tests.
- `./gradlew check` – run `build` plus Detekt, documentation, and verification tasks.
- `./gradlew :cql:compileKotlin` – compile the CQL multiplatform sources (parser/AST).
- `./gradlew :engine:test` or `:cql:jvmTest` – execute module-scoped test suites.
- `./gradlew :cql-to-elm-cli:run` – launch the CLI; `./gradlew clean` resets build outputs.

## Coding Style & Naming Conventions
- Kotlin code must pass Detekt (`config/detekt/detekt.yml`) and uses four-space indentation.
- Types use `UpperCamelCase`; functions, properties, and parameters use `lowerCamelCase`.
- Implementation-specific classes are suffixed with their domain (e.g., `ElmModelResolver`).
- Prefer Kotlin null-safety idioms over platform types; add comments sparingly and only for non-obvious intent.

## Testing Guidelines
- Tests use JUnit Jupiter with Kotlin assertions; name files `*Test.kt` and methods descriptively (e.g., `fun evaluatesNullCoalesce()`).
- Keep fixtures in `src/test/resources` alongside the owning module.
- Run `./gradlew test` or targeted commands (e.g., `./gradlew :shared:test`) before submitting changes.
- Maintain JaCoCo coverage (`./gradlew jacocoTestReport`) and flag notable drops in reviews.

## Commit & Pull Request Guidelines
- Write imperative, concise commit subjects patterned after existing history (`Fix playground deployment action (#1628)`).
- Reference issues or PR numbers in parentheses where applicable.
- PR descriptions should outline the problem, summarize the solution, and list verification steps (commands or screenshots).
- Ensure CI is green prior to requesting review and note any follow-up work or known limitations.
