# Proposal: Flatten repository structure around Kotlin Multiplatform

- Status: Proposed
- Date: 2026-04-22
- Related ADR: [ADR 004](../DECISIONS.md#adr-004-flatten-repository-structure-around-kotlin-multiplatform)

## Background

The repository's top-level layout dates from ~2014 when this directory was a clearing house for CQL tooling across many languages. Since then:

- `cql-execution` (CoffeeScript) moved to [its own repo][cql-execution]; `Src/coffeescript/` is a stub.
- The C# implementation moved out; `Src/dotnet/` is dormant.
- `Src/sql/` is a single 10-year-old orphan file.
- `Src/ide-support/BBEdit/` was abandoned.
- `Src/java-quickstart/` is a 12-file, 184K pedagogical project whose README still references ANTLR 4.3 incompatibilities (current project is 4.13). Not a dependency of anything.
- `Documents/` holds 2014-2015 `.pptx` / `.vsdx` / `.docx` decks.
- `Src/java/` quietly became the actual project — yet it is KMP, so the `java` name is a misnomer. It already produces JS and WASM targets.
- `Src/js/cql-to-elm-ui/` (Next.js playground) consumes the Kotlin/JS outputs via a `file:../../java/build/js/packages/engine` npm dependency — brittle and outside the Gradle graph.

[cql-execution]: https://github.com/cqframework/cql-execution

## Goals

1. **Promote the Gradle project to the repository root.** No more `cd Src/java` in every contributor command and CI step.
2. **Fold the JS projects into the Gradle build** as proper modules so the playground and example apps participate in `./gradlew build` and receive their Kotlin/JS inputs via project dependencies, not `file:` npm paths.
3. **Compile the CQL samples as part of the build.** Every `.cql` file under `examples/cql/` gets translated, so grammar and type-system changes can't silently rot the sample set.
4. **Archive dormant content** in a single obvious `archive/` folder.
5. **Elevate build inputs** (`grammar/`, `schemas/`) out of `Src/`, since the Gradle build references them directly.

## Classification

### Stays active (promoted)

| Path today | What it is | Decision |
| --- | --- | --- |
| `Src/java/*` | Root Gradle build (KMP) | Promote to root |
| `Src/grammar/` | ANTLR `.g4` files — referenced by `:cql` via `../../grammar` | Promote to `grammar/` |
| `Src/cql-lm/schema/` | ELM + modelinfo XSDs — referenced by `XsdKotlinGenTask` and `XSDValidationTest` | Promote to `schemas/` |
| `Src/cql-lm/uml/` | `ELM.eap`, `elm.xmi` — schema source-of-truth | Promote to `schemas/uml/` |
| `Src/js/cql-to-elm-ui/` | Next.js playground | Gradle module `:js:playground` |
| `Src/js/cql/` | `@cqframework/cql` npm re-export | Gradle module `:js:npm-cql` |
| `Src/js/examples/` | `cql2elm-browser`, `cql2elm-nodejs` | Gradle modules under `:examples:js:*` |
| `Examples/` | ~73 `.cql` / `.xml` samples | Move to `examples/cql/`; compile in build |

### Moves to `archive/`

| Path today | Why |
| --- | --- |
| `Documents/` | 2014-2015 pptx/docx/vsdx decks |
| `Src/coffeescript/cql-execution/` | Project moved to its own repo |
| `Src/dotnet/` | C# implementation moved out |
| `Src/sql/UncertaintyFunctions.sql` | Orphan file |
| `Src/ide-support/BBEdit/` | Abandoned |

### Deleted outright

| Path today | Why delete, not archive |
| --- | --- |
| `Src/java-quickstart/` | 12 files, 184K, purely pedagogical; has its own stale duplicate `cql.g4`; README references ANTLR 4.3 (project uses 4.13). No dependents. Git history preserves it. |

## Before / after code maps

### Before

```
clinical_quality_language/
├── AGENTS.md, CLAUDE.md → AGENTS.md, README.md, LICENSE, ...
├── Documents/                    # 2014-era pptx/docx
├── Examples/                     # 73 .cql / .xml samples
├── codecov.yaml                  # path filter: Src/java
├── .github/workflows/            # working-directory: ./Src/java
└── Src/
    ├── grammar/                      # cql.g4, fhirpath.g4, targetmap.g4
    ├── cql-lm/{schema/, uml/, cql-lm.spp}
    ├── coffeescript/cql-execution/   # dormant
    ├── dotnet/                       # dormant C# impl
    ├── sql/UncertaintyFunctions.sql  # orphan
    ├── ide-support/BBEdit/           # abandoned
    ├── java-quickstart/              # obsolete tutorial
    ├── js/
    │   ├── cql/                      # @cqframework/cql npm re-export
    │   ├── cql-to-elm-ui/            # Next.js; "file:../../java/build/..." npm deps
    │   └── examples/{cql2elm-browser, cql2elm-nodejs}
    └── java/                         # ← the actual root Gradle project
        ├── settings.gradle.kts       # rootProject.name = "cql-all"
        ├── build-logic/
        ├── cql/  elm/  shared/  engine/  engine-fhir/
        ├── cql-to-elm/  cql-to-elm-cli/  elm-fhir/
        ├── cqf-fhir/  cqf-fhir-npm/  quick/  qdm/  ucum/
        ├── cql-bom/  docs/
        └── tools/{cql-formatter, cql-parsetree, rewrite, xsd-to-modelinfo}
```

### After

```
clinical_quality_language/
├── AGENTS.md, CLAUDE.md → AGENTS.md, README.md, LICENSE, ...
├── codecov.yaml                  # path filter: (root)
├── settings.gradle.kts, build.gradle.kts, gradle.properties, gradlew, gradle/
├── build-logic/
├── config/
│
├── grammar/                      # cql.g4, fhirpath.g4, targetmap.g4
├── schemas/
│   ├── elm/  model/  common/
│   ├── uml/                      # ELM.eap, elm.xmi
│   └── cql-lm.spp
│
├── cql/  elm/  shared/  engine/  engine-fhir/
├── cql-to-elm/  cql-to-elm-cli/  elm-fhir/
├── cqf-fhir/  cqf-fhir-npm/  quick/  qdm/  ucum/
├── cql-bom/  docs/
├── tools/{cql-formatter, cql-parsetree, rewrite, xsd-to-modelinfo}
│
├── js/                           # JS surface, wired into Gradle
│   ├── npm-cql/                  # was Src/js/cql              → :js:npm-cql
│   └── playground/               # was Src/js/cql-to-elm-ui    → :js:playground
│
├── examples/
│   ├── cql/                      # was /Examples (73 samples)
│   │   └── build.gradle.kts      # :examples:cql:compile runs each through cql-to-elm
│   └── js/
│       ├── browser/              # :examples:js:browser
│       └── nodejs/               # :examples:js:nodejs
│
├── .github/workflows/            # working-directory dropped
└── archive/
    ├── documents/                # was /Documents
    ├── coffeescript/             # was Src/coffeescript/cql-execution
    ├── dotnet/                   # was Src/dotnet
    ├── sql/                      # was Src/sql
    └── bbedit/                   # was Src/ide-support/BBEdit
```

## Concrete edits

1. **`settings.gradle.kts`** — `include(...)` grows with `"js:npm-cql"`, `"js:playground"`, `"examples:cql"`, `"examples:js:browser"`, `"examples:js:nodejs"`.
2. **`cql/build.gradle.kts:18`** — `fileTree("../../grammar")` → `fileTree("../grammar")`.
3. **`build-logic/src/main/kotlin/XsdKotlinGenTask.kt:28,34`** — `../../cql-lm/schema/...` → paths under `../schemas/...`.
4. **`elm/src/jvmTest/.../XSDValidationTest.kt:60`** — same path update.
5. **`.github/workflows/build.yml`, `check-pr.yml`, `spotless.yml`, `codeql.yml`** — drop every `working-directory: ./Src/java`; update `codeql.yml` `source-root` and path filters.
6. **`codecov.yaml`** — drop the `"Src/java"` path component.
7. **Root `README.md`** — drop the three `Src/java/...` / `Src/java-quickstart/...` links; merge the module overview + build commands currently in `Src/java/README.md` into the root; delete `Src/java/README.md`.
8. **`AGENTS.md`** — drop `cd Src/java` from every example; remove the "not actively built by CI" paragraph (those paths are now archived or deleted); update `Src/grammar/` → `grammar/`, `Src/cql-lm/` → `schemas/`.
9. **`js/playground/package.json`** — `"cql-js": "file:../../java/build/js/packages/engine"` → `"file:../../build/js/packages/engine"` (same for `cql-wasm-js`). These paths become transient once step 11 wires real Gradle dependencies.
10. **Hardcoded GitHub URLs** — `js/playground/cql/supported-models.ts` (24 URLs) and `examples/js/browser/providers.js` (3 URLs) reference `raw.githubusercontent.com/.../main/Src/java/cql/...` and `.../Src/java/quick/...`. Update to `.../main/cql/...` and `.../main/quick/...`. Same fix in `js/npm-cql/README.md`.
11. **`cql/README.md:7`, `cql/AST.md`** — update `../../grammar` / `../grammar` references to match the new layout.
12. **Delete `Src/java-quickstart/`** and its reference in root `README.md`.

## New Gradle wiring for JS (sketch)

The JS-as-Gradle-module work is the biggest new piece:

- **`:js:playground`** uses `com.github.node-gradle.node`. `npmInstall` dependsOn `:engine:jsBrowserProductionLibraryDistribution` (and WASM equivalent). `next build` runs as a Gradle task. The `file:../../build/...` npm paths become transient scaffolding; real wiring is through Gradle project dependencies.
- **`:js:npm-cql`** publishes `@cqframework/cql` from the Kotlin/JS outputs of `:cql`, `:cql-to-elm`, `:elm`, `:engine`, `:shared`. Thin packaging module.
- **`:examples:cql:compile`** iterates `examples/cql/**/*.cql` and runs each through `cql-to-elm-cli` (or the programmatic API). Failure on any sample fails the build. This is what keeps examples fresh.
- **`:examples:js:browser` / `:examples:js:nodejs`** — same `node-gradle` treatment as the playground.

## Rollout (3 PRs)

1. **Pure archive + deletes** (`git mv` only; delete `java-quickstart`; update root `README.md` and `AGENTS.md` references to deleted/archived paths).
2. **`Src/java/*` → root** + grammar/schemas promotion + all path edits in Gradle files, CI, codecov, docs, and the `supported-models.ts` / `providers.js` URLs. Load-bearing change; build must be green at the end.
3. **JS + examples Gradle modules** — adds `node-gradle`, defines new `include(...)` entries, wires `:examples:cql:compile`. Can slip without blocking #2.

PR-per-step so #2 lands fast and the new Gradle wiring in #3 can iterate.

## Open questions

- **Rename `rootProject.name`?** Currently `"cql-all"`; `"clinical-quality-language"` reads better. Doesn't affect published artifact coordinates.
- **`examples/cql/` or `cql-examples/`?** Under `examples/` is clearer but deeper; leaning `examples/cql/`.
- **Delete vs archive** for coffeescript / dotnet? They're arguably just git-history material. Archiving is cheaper than deciding per-path.

## Risks

- **External links into `raw.githubusercontent.com/.../main/Src/java/...`** (modelinfo and FHIRHelpers files consumed by downstream projects) will break on merge. A one-time FYI to downstream repos covers this; the URLs are stable going forward.
- **Large diff.** Git rename detection keeps blame intact, but reviewers will see a wall of moves. Sequencing as three PRs (above) mitigates.
- **IDE project files** (`.idea`, `.vscode`) reference the old layout locally. These are gitignored for most of their contents; contributors re-import once.
