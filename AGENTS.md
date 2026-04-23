# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is the **Clinical Quality Language (CQL)** reference implementation — an HL7 standard for expressing clinical knowledge used in Clinical Decision Support (CDS) and Clinical Quality Measurement (CQM). The repo contains a CQL compiler/translator (CQL-to-ELM), an ELM runtime engine, and supporting tooling.

## Build System

The primary source is under `Src/java/` and uses **Gradle** (Kotlin DSL) with a Gradle wrapper. All Gradle commands must be run from `Src/java/`.

```bash
cd Src/java

# Full build (compile, test, check)
./gradlew build

# Clean
./gradlew clean

# Run a single module's tests
./gradlew :engine:test
./gradlew :cql-to-elm:test

# Run a single test class
./gradlew :engine:test --tests "org.opencds.cqf.cql.engine.execution.CqlArithmeticFunctionsTest"

# Run a single test method
./gradlew :engine:test --tests "org.opencds.cqf.cql.engine.execution.CqlArithmeticFunctionsTest.testAdd"
```

**Java version:** JDK 17 (toolchain target), CI runs on JDK 21.

## Versioning

The project version is derived from git state at configuration time (see `Src/java/build-logic/src/main/kotlin/GitVersion.kt` and the `cql.git-version` precompiled plugin). There is no hardcoded `version` in `gradle.properties`. Rules:

1. Tag `vX.Y.Z` at HEAD → `X.Y.Z` (release, signed on publish)
2. On `main`, no tag → latest `vX.Y.Z` tag with minor bumped, patch reset to 0, `-SNAPSHOT` appended
3. Any other branch / detached HEAD → `<bumped>-<sanitized-branch>-<short-sha>-SNAPSHOT`

Releases are cut by pushing a `vX.Y.Z` tag. See `Src/java/README.md` for the full description.

## Formatting

Uses **Spotless** with Palantir Java Format and ktfmt (kotlinlang style). Generated code (`**/generated/**`) is excluded.

```bash
cd Src/java

# Check formatting (runs on PRs in CI)
./gradlew spotlessCheck

# Auto-fix formatting
./gradlew spotlessApply
```

## Static Analysis

- **detekt** for Kotlin (config at `Src/java/config/detekt/detekt.yml`, per-project baselines at `config/detekt-baseline.xml`)
- **SonarCloud** runs on main branch builds
- **Animal Sniffer** checks Android API compatibility

## Architecture

### Gradle Modules (under `Src/java/`)

The project is a multi-module Gradle build. Shared build conventions live in `build-logic/`:

- **cql** — ANTLR4 lexer/parser from grammar (`Src/grammar/cql.g4`, `fhirpath.g4`), plus Kotlin classes for the CQL/ELM type system
- **elm** — Kotlin classes generated from the ELM XML schema (XJC)
- **shared** — Common utilities used across modules
- **cql-to-elm** — The CQL compiler/translator: parses CQL, performs semantic analysis, outputs ELM (XML/JSON)
- **cql-to-elm-cli** — CLI wrapper for the translator
- **engine** — ELM runtime that evaluates CQL expressions (the "CQL engine")
- **engine-fhir** — FHIR-specific data provider and model bindings for the engine
- **elm-fhir** — Data requirements processing and FHIR utilities
- **cqf-fhir** / **cqf-fhir-npm** — FHIR package/NPM support
- **quick** — Schema and model info for QUICK, FHIR, QI-Core, US Core; includes FHIRHelpers library
- **qdm** — Schema and model info for QDM
- **ucum** — UCUM (units of measure) service
- **tools:cql-formatter** — CQL source code formatter
- **tools:cql-parsetree** — Debug tool for CQL parse trees
- **tools:rewrite** — OpenRewrite recipes for automated refactoring
- **tools:xsd-to-modelinfo** — Generates model info from XSD

### Key Data Flow

1. **CQL source** → ANTLR4 parser (grammar in `Src/grammar/`) → parse tree
2. Parse tree → **cql-to-elm** translator → ELM (Expression Logical Model) representation
3. ELM → **engine** runtime → evaluated results (with data provided via model-specific providers like engine-fhir)

### Language & Framework

The codebase is **Kotlin/JVM** with some Java. Tests use **JUnit 5** with Hamcrest matchers. The `cql` and `elm` modules include Kotlin Multiplatform support (JS/WASM targets for the CQL playground at `Src/js/cql-to-elm-ui`).

### ANTLR Grammar

The CQL and FHIRPath grammars are in `Src/grammar/`. Changes to `.g4` files trigger ANTLR code generation during the build.

## Other Source Directories

- `Src/js/` — JavaScript/TypeScript CQL playground (Next.js)
- `Src/coffeescript/`, `Src/dotnet/`, `Src/sql/` — Alternative language implementations (not actively built by CI)
- `Examples/` — Sample CQL files and their ELM translations
- `Src/cql-lm/` — CQL Logical Model schemas
