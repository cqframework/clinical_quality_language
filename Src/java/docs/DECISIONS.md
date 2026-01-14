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

## ADR 002: Developer Documentation
- Status: Proposed
- Date: 2025-01-14
- Context: The project reached a complexity threshold where informal documentation was no longer sufficient. Decisions were being revisited repeatedly without a record of prior reasoning. A PR review requested design documentation, but there was no conventional place for it. We needed lightweight structure to communicate expectations without adding tooling overhead.
- Decision: Adopt a minimal docs-as-code approach: a `docs/` folder in the repository with an append-only ADR log and Gradle-based snippet extraction to keep code examples compiling. Module-specific docs remain in their respective directories. A Gradle `buildDocs` task assembles the site (copying module docs, injecting snippets, generating index) into `build/site/`, which GitHub Actions deploys to a `gh-pages` branch for GitHub Pages serving.
- Consequences: Documentation lives close to code and is more likely to stay current. Code snippets are compiled as part of the build, ensuring examples don't rot. No new tools to learn beyond Markdown and Gradle. Requires discipline to update docs alongside code changes.
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