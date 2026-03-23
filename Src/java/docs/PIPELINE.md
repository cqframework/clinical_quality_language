# CQL Compilation Pipeline

Architecture and design of the CQL-to-ELM compilation pipeline.

> **Date:** 2026-03-19
> **Companion docs:** [AST Design](../cql/AST.md) ·
> [ADR 003](DECISIONS.md#adr-003-cql-ast-representation) ·
> [Legacy Issues](LEGACY_ISSUES.md)

---

## Pipeline Overview

The compiler transforms CQL source into fully typed ELM through five phases:

```
CQL Source
  │
  ▼
┌──────────────────────────────────────────────────────┐
│  1. PARSE                                            │
│     ANTLR Lexer/Parser → Parse Tree → AST Builder    │
│     Output: CQL AST (immutable data classes)         │
└──────────────────────┬───────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────┐
│  2. ANALYSIS (convergence loop)                      │
│     SymbolCollector → SymbolTable                    │
│     TypeResolver + ConversionAnalyzer → SemanticModel │
│     SemanticValidator → diagnostics                  │
│     Output: AST + SemanticModel (types, conversions) │
└──────────────────────┬───────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────┐
│  3. LOWERING                                         │
│     AST + SemanticModel → Lowered AST                │
│     Structural rewrites: phrases → operator trees    │
│     Output: Lowered AST (same node types, desugared) │
└──────────────────────┬───────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────┐
│  4. EMISSION                                         │
│     Lowered AST + SemanticModel → ELM                │
│     Mechanical 1:1 mapping, no semantic decisions    │
│     Output: ELM Library                              │
└──────────────────────┬───────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────┐
│  5. POST-PROCESSING                                  │
│     resultType, locators, annotations, signatures    │
│     Output: ELM Library (annotated per options)      │
└──────────────────────────────────────────────────────┘
```

### Key Distinction: Conversions vs Lowering

The pipeline separates two fundamentally different transformations:

**Type conversions** (Analysis phase → SyntheticTable):
- Driven by type inference. An Integer operand where Decimal is expected
  → record `OperatorConversion("ToDecimal")`.
- Participate in the convergence loop: inserting a conversion changes types,
  which may trigger further conversions.
- Applied during emission by wrapping ELM operands.
- Examples: ToDecimal, ImplicitCast, ListConversion, IntervalConversion.

**Structural lowering** (Lowering phase → AST rewrite):
- Driven by CQL syntax. `within 3 days of` ALWAYS expands to
  `In(x, Interval[Subtract(start, qty), Add(end, qty)])` regardless of types.
- Reads types from the SemanticModel (e.g., "is this an interval?") but
  does not change types.
- Produces a new AST using existing node types.
- Examples: boundary selectors → Start/End, point-interval promotion,
  phrase expansion, operator rename (Add→Concatenate), Coalesce wrapping
  for CONCAT, system function mapping (CalculateAgeInYears→CalculateAge).

Mixing these two in the same mechanism (as the legacy translator does)
creates ordering dependencies and semantic logic leaking into emission.

---

## Phase 1: Parse

**Module:** `cql` (Kotlin Multiplatform: JVM, JS, WASM)

ANTLR lexer/parser produces a parse tree. The AST Builder (`Builder.kt`)
transforms it into immutable Kotlin data classes. The AST represents CQL
source structure faithfully — no desugaring, no type information.

**Output:** `Library` (immutable AST data classes in `org.hl7.cql.ast`)

---

## Phase 2: Analysis

**Package:** `org.cqframework.cql.cql2elm.analysis`

Analysis produces the `SemanticModel` — a collection of side tables keyed
by AST node identity. The AST is never mutated.

### Components

| Component | Role |
|-----------|------|
| `SymbolCollector` | Walks AST, builds `SymbolTable` (definitions, scopes) |
| `TypeResolver` | Bottom-up type inference + overload resolution → `TypeTable` |
| `ConversionAnalyzer` | Records implicit conversions in `SyntheticTable` |
| `SemanticValidator` | Detects errors, flags them in `SemanticModel` |

### Convergence Loop

Type inference and conversion analysis iterate until stable:

```
symbols = SymbolCollector.collect(library)
syntheticTable = SyntheticTable()

for iteration in 1..maxIterations:
    typeTable = TypeResolver(operatorRegistry, syntheticTable)
                    .resolve(library, symbols)
    analyzer = ConversionAnalyzer(typeTable, operatorRegistry, syntheticTable)
    analyzer.analyzeLibrary(library)
    if analyzer.newSyntheticsInserted == 0: break

finalTypeTable = TypeResolver(operatorRegistry, syntheticTable)
                     .resolve(library, symbols)
semanticModel = SemanticModel(symbols, finalTypeTable, syntheticTable, ...)
SemanticValidator.validate(library, symbols, semanticModel)
```

**Why iterate?** Inserting a conversion changes effective types. Example:
`Avg({1,2,3})` — the list conversion `List<Integer>→List<Decimal>` changes
the argument type, which the TypeResolver needs to see to resolve the Avg
overload correctly.

**Why no AST mutation?** The AST stays immutable. The `SyntheticTable`
records conversions keyed by `(parentExpression, slot)` using an
`IdentityHashMap`. The TypeResolver reads effective types from the
SyntheticTable when resolving overloads. Benefits:
- SymbolTable collected once (no re-collection)
- No identity-tracking bugs
- No idempotency guards
- Editor sees both source types and post-conversion types

### SyntheticTable

Records implicit type conversions only:

```kotlin
sealed interface Synthetic {
    data class OperatorConversion(val operatorName: String) : Synthetic
    data class ImplicitCast(val targetType: DataType) : Synthetic
    data class ListConversion(val innerOperatorName: String) : Synthetic
    data class ListDemotion(val targetElementType: DataType, ...) : Synthetic
    data class IntervalConversion(val innerOperatorName: String) : Synthetic
}
```

Keyed by `(Expression, Slot)` where Slot identifies which operand:
`Left`, `Right`, `Operand`, `Argument(i)`, `ListElement(i)`,
`IntervalLow`, `IntervalHigh`, `ThenBranch`, `ElseBranch`,
`CaseBranch(i)`, `CaseCondition(i)`.

### Type Inference

The `TypeResolver` implements `ExpressionFold<DataType?>` — a catamorphism
that types children before parents. Key rules:

- **Literals:** type from literal kind (IntLiteral→Integer, etc.)
- **Identifiers:** resolve via SymbolTable scope chain
- **Operators:** resolve via OperatorRegistry, record resolution
- **Function calls:** resolve via OperatorRegistry or user-defined functions
- **Queries:** scoped inference (aliases, lets, aggregate accumulators)
- **Effective types:** when the SyntheticTable has a conversion for a slot,
  the effective type (post-conversion) is used for overload resolution

### Semantic Error Classification

| Phase | Errors |
|-------|--------|
| COLLECT | Duplicate definitions, unresolved models/includes |
| INFER | Unresolved identifiers, no matching operator/function, circular refs |
| CONVERT | No valid conversion path |
| VALIDATE | Invalid casts, unresolved properties, name hiding |

---

## Phase 3: Lowering

**Class:** `Normalizer` in `org.cqframework.cql.cql2elm.analysis`

**Status:** Implemented. Structural rewrites consolidated in `Normalizer.kt`
(~960 lines). Invoked by SemanticAnalyzer after the convergence loop.

Lowering transforms complex CQL phrases into simpler AST trees using
existing node types. It reads the SemanticModel for structural type
information (is this an interval? a point?) but does not modify types.

### What Gets Lowered

| CQL Phrase | Lowered To |
|------------|-----------|
| `A before B` | `Before(A, B)` with boundary Start/End extraction |
| `A 3 days before B` | `SameAs(End(A), Subtract(Start(B), 3d))` |
| `A 3 days or more before B` | `SameOrBefore(End(A), Subtract(Start(B), 3d))` |
| `A within 3 days of B` | `In(A, Interval[Sub(Start(B),3d), Add(End(B),3d)])` |
| `start of A before B` | Boundary selector → `Start(A)`, then `Before(Start(A), B)` |
| Point vs interval | Point → `If(IsNull(p), Null, Interval[p,p])` |
| `x & y` | CONCAT → `Concatenate(Coalesce(x,''), Coalesce(y,''))` |
| `x + y` (strings) | Add → Concatenate (operator rewrite) |
| `CalculateAgeInYears(d)` | `CalculateAge(ToDate(d), Year)` |
| `AgeInYears()` | `CalculateAge(Patient.birthDate)` (context injection) |

### Why AST→AST (not new IR types)

The lowered AST uses existing node types:
- `TimeBoundaryExpression(START/END, operand)` for boundary extraction
- `OperatorBinaryExpression(SUBTRACT/ADD, ...)` for quantity arithmetic
- `MembershipExpression(IN, ...)` for within expansion
- `IfExpression` for null-safety promotion
- `LiteralExpression(IntervalLiteral(...))` for constructed intervals
- `FunctionCallExpression` for renamed system functions

No new IR types needed. The lowered AST is typed by running the
TypeResolver once more on the new nodes (children are already typed
from the analysis phase).

### Lowering Reads Types

Lowering needs structural type information from the SemanticModel:
- "Is B an interval?" → extract Start/End
- "Is this operand a point against an interval?" → promote to interval
- "Is the right operand of Contains a list or a point?" → Contains vs Includes

This is read-only access to types — lowering never changes types or
records conversions.

---

## Phase 4: Emission

**Package:** `org.cqframework.cql.cql2elm.codegen`

After lowering, every AST node maps 1:1 to an ELM node. Emission is
purely mechanical — no `semanticModel` type lookups, no conditional
logic, no wrapping.

The `EmissionContext` implements `ExpressionFold<ElmExpression>`. Each
handler creates the corresponding ELM node and applies any synthetics
from the SyntheticTable (type conversions recorded during analysis).

```kotlin
override fun onBinaryOperator(expr, left, right) =
    emitBinaryOperator(expr,
        applySynthetics(expr, Slot.Left, left),
        applySynthetics(expr, Slot.Right, right))
```

`applySynthetics` wraps an ELM expression in conversion nodes:
- `OperatorConversion("ToDecimal")` → `ToDecimal(operand)`
- `ImplicitCast(Decimal)` → `As(operand, Decimal)`
- `ListConversion("ToDecimal")` → `Query(source=list, return=ToDecimal(X))`

---

## Phase 5: Post-Processing

**Status:** Partially implemented

Adds output annotations controlled by compiler options:

| Option | Effect |
|--------|--------|
| `EnableResultTypes` | `resultTypeName` / `resultTypeSpecifier` on ELM nodes |
| `EnableLocators` | Source location on ELM nodes |
| `EnableAnnotations` | Narrative annotations |
| `signatureLevel` | Function/operator signatures |

---

## Compiler Option Classification

| Category | Phase | Examples |
|----------|-------|---------|
| **Semantic** | Analysis | `DisableListPromotion`, `EnableIntervalDemotion`, `compatibilityLevel` |
| **Output** | Post-processing | `EnableResultTypes`, `EnableLocators`, `signatureLevel` |
| **Orchestration** | Pipeline | `verifyOnly`, `analyzeDataRequirements` |

---

## Current Status

### OperatorTests Parity: 30/32 passing

| Status | Tests |
|--------|-------|
| **Passing (30)** | AgeOperators, AggregateOperators, ArithmeticOperators, ComparisonOperators, CqlComparisonOperators, CqlIntervalOperators, CqlListOperators, DateTimeOperators, ForwardReferences, Functions, ImplicitConversions, IntervalOperatorPhrases, IntervalOperators, InvalidCastExpression, InvalidSortClauses, ListOperators, LogicalOperators, MessageOperators, NameHiding, NullologicalOperators, Query, RecursiveFunctions, Sorting, StringOperators, TerminologyReferences, TimeOperators, TupleAndClassConversions, TypeOperators, UndeclaredForward, UndeclaredSignature |
| **Legacy bug (skip)** | Aggregate (#1710 — our type inference is more correct) |
| **Error recovery (skip)** | MultiSourceQuery (legacy replaces type errors with Null) |

### Exploratory Suite Triage (2026-03-23)

Enabled all three `@Disabled` test factories.

Initial triage: 53 passed, 59 failed, 39 skipped.
After quick fixes: root-level 23 → 26 pass.
After library include resolution: root-level 26 → 36 pass, FHIR R4 0 → 0 pass (8 fail,
6 skip), FHIR R4.0.1 unchanged (3 pass, 16 fail, 9 skip).

#### Root-level tests (77 files): 36 pass, 20 fail, 21 skip

#### FHIR R4 tests (14 files): 0 pass, 8 fail, 6 skip

#### FHIR R4.0.1 tests (28 files): 3 pass, 16 fail, 9 skip

#### Failure Categories

| Category | Count | Root Cause | Fix Strategy |
|----------|-------|------------|--------------|
| **Library includes** (resolved) | 10 newly passing | Cross-library refs now emit `FunctionRef`/`ExpressionRef` with `libraryName`. `TestLibrarySourceProvider` wired into parity test. | `Resolution.IncludeRef`, `SemanticValidator` skip for cross-library calls |
| **ELM content diffs** | 20 fail | Detailed subcategories below | Mixed — see subcategories |

##### ELM Content Difference Subcategories

| Subcategory | Tests | Issue |
|-------------|-------|-------|
| ~~Escaped quotes in identifiers~~ | ~~Issue827, TestQuotedForwards~~ | **Fixed** — `unescapeCql()` applied in emission |
| ~~DateTime timezone `0` vs `0.0`~~ | ~~DateTimeLiteralTest~~ | **Fixed** — `BigDecimal("0.0")` for UTC offset |
| ~~`In` vs `InValueSet`/`InCodeSystem`~~ | ~~InCodeSystemTest, InValueSetTest~~ | **Fixed** — Specialized terminology operator emission |
| ~~`Retrieve.codes` not emitted~~ | ~~CMS146v2_Test_CQM~~ | **Fixed** — SymbolTable-based terminology resolution in RetrieveEmission |
| Cross-library implicit conversions | Median_dup_vals_odd, Median_odd, TupleDifferentKeys, UncertTuplesWithDiffNullFields | Included library's function signatures not available for conversion resolution |
| Error recovery (expressions → Null) | IdentifierDoesNotResolve..., Issue616, TestIdentifierCaseMismatch | New pipeline preserves invalid expressions; legacy replaces with Null |
| Local function → system function resolution | LocalFunctionResolutionTest | System function `ToDate` emitted as `FunctionRef` |
| Query vs inline form | Issue587, SignatureResolutionTest | New pipeline emits Query where legacy inlines; or vice versa |
| Date vs ToDateTime promotion | Issue863 | New pipeline emits `Date` literal; legacy wraps in `ToDateTime` |
| QDM model class resolution | TestChoiceAssignment | QDM class type differs — model-specific resolution gap |
| Context definition synthesis | TestEncounterParameterContext | New pipeline doesn't synthesize context definitions (e.g., `define Encounter: SingletonFrom([Encounter])`) |
| Point-interval promotion | TestPointIntervalSignatures | `If(IsNull...)` point promotion not applied |

#### Skip Categories

| Category | Count | Issue |
|----------|-------|-------|
| ~~ModelManager required~~ | ~~12~~ | **Fixed** — post-normalization TypeResolver was missing `modelContext`. Analysis bug, not emission. |
| Could not resolve type/model | 8 | `Could not resolve` — model types not available (Patient context, related context, etc.) |
| Unknown system type | 8 | `Unknown system type: ''` — empty type from analysis edge cases |
| AST Builder problems | 3 | CQL files with intentional errors; Builder rejects them |
| AST Builder parse error | 2 | Escape sequence handling not implemented in Builder |
| Unsupported AST node | 2 | Unit conversion, timezone component not yet emitted |
| FHIR model version mismatch | 1 | FHIR version not matched in ModelManager |
| Argument count mismatch | 1 | Expected 1 arg, got more — FHIR helpers call shape |

#### Priority Order for Closing Gaps

1. **Cross-library implicit conversions** (4+ tests) — wire `CompiledLibrary.operatorMap` into TypeResolver for included libraries
2. **Error recovery parity** (3+ tests) — case-insensitive identifier matching, Null replacement for unresolved identifiers
3. **Context definition synthesis** (1+ tests) — synthesize `define <Context>` expressions
4. **Point-interval promotion** (1+ tests) — apply `If(IsNull, Null, Interval[p,p])` lowering
5. **Unknown system types** (8 skips) — fix empty type strings from analysis

### Implementation Status

| Component | Status |
|-----------|--------|
| SymbolCollector | Done |
| TypeResolver | Done (bottom-up + overload resolution + effective types) |
| ConversionAnalyzer | Done (all conversion kinds via SyntheticTable) |
| SyntheticTable | Done (5 conversion types, convergence loop) |
| SemanticValidator | Partial (identifiers, casts, recursive functions) |
| Normalizer (lowering) | Done (phrase expansion, interval operators, boundary selectors, coalescing, operator rewrites) |
| Emission | Done (20 emission files, mechanical) |
| Post-processing | Partial (resultType decoration; no locators, annotations, or signatures yet) |

---

## Key File Locations

### Analysis

| Component | Path |
|-----------|------|
| SemanticAnalyzer | `cql-to-elm/.../analysis/SemanticAnalyzer.kt` |
| SemanticModel | `cql-to-elm/.../analysis/SemanticModel.kt` |
| TypeResolver | `cql-to-elm/.../analysis/TypeResolver.kt` |
| ConversionAnalyzer | `cql-to-elm/.../analysis/ConversionAnalyzer.kt` |
| Synthetic / SyntheticTable | `cql-to-elm/.../analysis/Synthetic.kt` |
| SemanticValidator | `cql-to-elm/.../analysis/SemanticValidator.kt` |
| OperatorRegistry | `cql-to-elm/.../analysis/OperatorRegistry.kt` |
| Normalizer (lowering) | `cql-to-elm/.../analysis/Normalizer.kt` |

### Codegen

| Component | Path |
|-----------|------|
| ElmEmitter | `cql-to-elm/.../codegen/ElmEmitter.kt` |
| EmissionContext | `cql-to-elm/.../codegen/EmissionContext.kt` |
| IntervalOperatorEmission | `cql-to-elm/.../codegen/IntervalOperatorEmission.kt` |
| SystemFunctionEmission | `cql-to-elm/.../codegen/SystemFunctionEmission.kt` |
| (20 total emission files) | `cql-to-elm/.../codegen/*Emission.kt` |

### Shared

| Component | Path |
|-----------|------|
| AST nodes | `cql/src/commonMain/kotlin/org/hl7/cql/ast/` |
| CQL type system | `cql/src/commonMain/kotlin/org/hl7/cql/model/` |
| System operators | `cql-to-elm/.../model/SystemLibraryHelper.kt` |
| Operator/Conversion model | `cql-to-elm/.../model/Operator.kt`, `Conversion.kt` |

### Tests

| Component | Path |
|-----------|------|
| Parity tests (common) | `cql-to-elm/src/commonTest/.../codegen/ElmEmitterParityTest.kt` |
| Full parity suite (JVM) | `cql-to-elm/src/jvmTest/.../codegen/FullParityTest.kt` |
| OperatorTests CQL files | `cql-to-elm/src/jvmTest/resources/.../OperatorTests/` |
