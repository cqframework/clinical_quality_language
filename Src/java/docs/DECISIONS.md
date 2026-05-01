<!--
This file is an append-only log of important (i.e. "irreversible") decisions.
Add new decisions to the top, just after this header.

"Irreversible" decisions are those whose reversal would be expensive due to cross-cutting impact
(e.g., architectural structure, data ownership, platform commitments, or cross-team operating models).
If changing the decision later would require coordinated work across teams, data migration, customer-facing changes, or executive alignment, it belongs in an ADR.

Template:
## ADR NNN: Short Title
- Status: Proposed | Accepted | Superseded | Reversed
- Date: YYYY-MM-DD
- Context: 2–4 sentences on the problem/constraints.
- Decision: The choice and scope; include any key parameters or versions.
- Consequences: What gets easier/harder; risks; follow-ups.
- Alternatives: Top 1–2 rejected options with a one-liner why not.
- References: Links to PRs/discussions/issues/specs; related ADRs. (optional)

-->

# Architecture Decision Record

## ADR 004: Flatten repository structure around Kotlin Multiplatform
- Status: Proposed
- Date: 2026-04-22
- Context: The repository layout dates from ~2014 when this directory served as a multi-language clearing house for CQL tooling. Since then `cql-execution` (CoffeeScript) and the C# implementation moved to their own repos, the SQL and BBEdit contributions were abandoned, and `Src/java/` quietly became the actual project — despite being a Kotlin Multiplatform build that targets JVM, JS, and WASM. Contributors cd into `Src/java/` for every command, and the Next.js playground consumes Kotlin/JS outputs through a fragile `file:../../java/build/...` npm dependency that sits outside the Gradle graph.
- Decision: Promote `Src/java/` to the repository root. Move build inputs (`Src/grammar/` → `grammar/`, `Src/cql-lm/schema/` → `schemas/`) alongside. Fold the JS playground and example apps into the Gradle build as modules (`:js:playground`, `:js:npm-cql`, `:examples:js:*`) that consume Kotlin/JS outputs via project dependencies. Move dormant language implementations and 2014-era documents into `archive/`. Delete `Src/java-quickstart/` (12-file pedagogical project with no dependents and stale ANTLR 4.3 references). Compile every sample in `examples/cql/` as part of the build so grammar or type-system changes can't silently rot the sample set.
- Consequences: Contributors no longer need `cd Src/java` for Gradle commands. The JS playground gains correct dependency wiring and is built by `./gradlew build`. Sample CQL cannot rot unnoticed. External links into `raw.githubusercontent.com/.../main/Src/java/...` (modelinfo, FHIRHelpers) break and require a one-time FYI to downstream repos. Rollout is a large diff even with git rename detection; planned as three PRs (archive/delete moves, root promotion + path fixes, JS Gradle modules) so the load-bearing change can land fast while Gradle-ification of the JS surface iterates.
- Alternatives:
  - Leave the structure as-is: Rejected because `Src/java/` actively misleads new contributors (the module set is not Java-only) and the `file:` npm deps remain an ongoing source of friction.
  - Rename `Src/java/` to `Src/kmp/` without flattening: Rejected because it preserves an unnecessary `Src/` level and does not address the JS-outside-Gradle problem.
  - Split into multiple repositories (engine, translator, playground): Rejected as overkill; modules are tightly coupled through the Kotlin/JS artifact outputs and share a release cycle.
- References: [Repo Restructure Proposal](proposals/repo-restructure.md) — full before/after maps, tactical edits, and rollout plan.

## ADR 003: CQL AST Representation
- Status: Proposed
- Date: 2025-01-21
- Context: The existing CQL-to-ELM compiler coverts CQL to ELM directly from the parse tree in a single pass without an intermediate AST. This intermingles parsing concerns with semantic processing (such as type inference, duplicate detection, etc.). The makes maintenance and extension more difficult, as changes to parsing logic can have unintended side effects on semantic processing and ELM generation. An explicit AST would separate these concerns, allowing for clearer, more modular code.
- Decision: Create an explicit AST representation of CQL. The AST will be constructed from the parse tree in a dedicated parsing phase, isolating syntactic concerns. Subsequent semantic analysis and ELM generation phases will operate on the AST, allowing for cleaner separation of responsibilities. The AST will be designed to capture all necessary syntactic and semantic information required for accurate ELM generation.
- Consequences: Editor tooling and analysis features can leverage the AST for more accurate diagnostics and refactorings. Future extensions to CQL (e.g. CQL 2.0) can be accommodated by modifying the AST structure without impacting parsing or ELM generation logic. Replacing ANTLR as the parser becomes possible. Initial development effort is required to design and implement the AST and refactor existing code to use it. Potential performance impact due to additional processing step, though this is mitigated by improved maintainability and extensibility.
- Alternatives: 
   - Continue using parse tree directly: Rejected due to maintenance challenges and difficulty in building editor tooling due to the lossy nature of ELM
   - Maintain multiple branches of the compiler for different versions of CQL: Rejected due to increased complexity and maintenance burden.
   - Use an existing intermediate representation (IR) framework (e.g. Apache Calcite): Rejected because existing IRs did not align well with CQL semantics and would require significant adaptation; loss of KMP compatibility.
- References: See the [AST](../cql/AST.md) design docs for more details. 

## ADR 002: Developer Documentation
- Status: Proposed
- Date: 2025-01-14
- Context: The project reached a complexity threshold where informal documentation was no longer sufficient. Decisions were being revisited repeatedly without a record of prior reasoning. A PR review requested design documentation, but there was no conventional place for it. We needed lightweight structure to communicate expectations without adding tooling overhead.
- Decision: Adopt a minimal docs-as-code approach: a `docs/` folder in the repository with an append-only ADR log and Gradle-based snippet extraction to keep code examples compiling. Module-specific docs remain in their respective directories. A Gradle `buildDocs` task assembles the site (copying module docs, injecting snippets, generating index) into `build/site/`, which GitHub Actions deploys to a `gh-pages` branch for GitHub Pages serving.
- Consequences: Documentation lives close to code and is more likely to stay current. Code snippets are compiled as part of the build, ensuring examples don't rot. No new tools to learn beyond Markdown and Gradle. Requires discipline to update docs alongside code changes. Markdown + Snippets approach is compatible with full static site generators if needs evolve.
- Alternatives:
  - Full documentation framework (e.g., MkDocs, Docusaurus): Rejected due to learning curve and additional build tooling that felt heavyweight for current needs.
  - Ad hoc documentation: Rejected because lack of structure made it unclear where docs should live and what was expected.
  - GitHub Wiki: Rejected because it lives separately from the codebase and historically drifts out of sync with the code.
  - GitHub Pages serving docs/ directly: Rejected because relative links to module docs outside docs/ would 404; assembly step needed.

## ADR 001: Kotlin Multiplatform
- Status: Accepted
- Date: 2024-06-01
- Context: The CQL tooling was originally implemented in Java, targeting only the JVM. There was growing demand to support additional platforms, particularly JavaScript for browser-based and Node.js environments. Maintaining a single codebase that could target multiple platforms was essential to avoid duplicating core logic.
- Decision: Adopt Kotlin Multiplatform (KMP) as the cross-platform strategy. Migrate core modules from Java to Kotlin, enabling compilation to JVM, JS, and potentially other targets from a shared codebase.
- Consequences: Enables JS and other platform targets without maintaining separate implementations. Requires learning Kotlin and KMP-specific patterns. Platform-specific code must be isolated using expect/actual declarations. Build complexity increases with multi-target configuration.
- Alternatives:
  - Maintain a separate JS implementation: Rejected due to significant code duplication and ongoing maintenance burden of keeping two implementations in sync.
  - Use a Java-to-JS transpiler (e.g., TeaVM): Rejected due to limited ecosystem support, potential runtime compatibility issues, and less active community compared to Kotlin Multiplatform.