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
│     TypeResolver + ConversionPlanner → SemanticModel │
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

**Type conversions** (Analysis phase → ConversionTable):
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
| `ConversionPlanner` | Records implicit conversions in `ConversionTable` |
| `SemanticValidator` | Detects errors, flags them in `SemanticModel` |

### Convergence Loop

Type inference and conversion analysis iterate until stable:

```
symbols = SymbolCollector.collect(library)
conversionTable = ConversionTable()

for iteration in 1..maxIterations:
    typeTable = TypeResolver(operatorRegistry, conversionTable)
                    .resolve(library, symbols)
    planner = ConversionPlanner(typeTable, operatorRegistry, conversionTable)
    planner.analyzeLibrary(library)
    if planner.newConversionsInserted == 0: break

finalTypeTable = TypeResolver(operatorRegistry, conversionTable)
                     .resolve(library, symbols)
semanticModel = SemanticModel(symbols, finalTypeTable, conversionTable, ...)
SemanticValidator.validate(library, symbols, semanticModel)
```

**Why iterate?** Inserting a conversion changes effective types. Example:
`Avg({1,2,3})` — the list conversion `List<Integer>→List<Decimal>` changes
the argument type, which the TypeResolver needs to see to resolve the Avg
overload correctly.

**Why no AST mutation?** The AST stays immutable. The `ConversionTable`
records conversions keyed by `(parentExpression, slot)` using an
`IdentityHashMap`. The TypeResolver reads effective types from the
ConversionTable when resolving overloads. Benefits:
- SymbolTable collected once (no re-collection)
- No identity-tracking bugs
- No idempotency guards
- Editor sees both source types and post-conversion types

### ConversionTable

Records implicit type conversions only:

```kotlin
sealed interface ImplicitConversion {
    data class OperatorConversion(val operatorName: String) : ImplicitConversion
    data class ImplicitCast(val targetType: DataType) : ImplicitConversion
    data class ListConversion(val innerOperatorName: String) : ImplicitConversion
    data class ListDemotion(val targetElementType: DataType, ...) : ImplicitConversion
    data class IntervalConversion(val innerOperatorName: String) : ImplicitConversion
}
```

Keyed by `(Expression, ConversionSlot)` where ConversionSlot identifies which operand:
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
- **Effective types:** when the ConversionTable has a conversion for a slot,
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

**Class:** `Lowering` in `org.cqframework.cql.cql2elm.analysis`

**Status:** Implemented. Structural rewrites consolidated in `Lowering.kt`
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
handler creates the corresponding ELM node and applies any conversions
from the ConversionTable (type conversions recorded during analysis).

```kotlin
override fun onBinaryOperator(expr, left, right) =
    emitBinaryOperator(expr,
        applyConversions(expr, ConversionSlot.Left, left),
        applyConversions(expr, ConversionSlot.Right, right))
```

`applyConversions` wraps an ELM expression in conversion nodes:
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

### OperatorTests Parity: 29/32 passing, 3 known skips (+ 2 FHIR R4 choice type tests)

| Status | Tests |
|--------|-------|
| **Passing (29 + 2 FHIR)** | AgeOperators, AggregateOperators, ArithmeticOperators, ComparisonOperators, CqlComparisonOperators, CqlIntervalOperators, CqlListOperators, DateTimeOperators, ForwardReferences, Functions, ImplicitConversions, IntervalOperatorPhrases, IntervalOperators, InvalidCastExpression, InvalidSortClauses, ListOperators, LogicalOperators, MessageOperators, NameHiding, NullologicalOperators, Query, Sorting, StringOperators, TerminologyReferences, TimeOperators, TupleAndClassConversions, TypeOperators, UndeclaredForward, UndeclaredSignature, **TestChoiceTypes**, **TestChoiceDateRangeOptimization** |
| **Legacy bug (skip)** | Aggregate (#1710 — our type inference is more correct) |
| **Error recovery (skip)** | MultiSourceQuery (legacy replaces type errors with Null) |
| **Recursive (skip)** | RecursiveFunctions (new pipeline correctly resolves recursive types) |

### Exploratory Suite Triage (updated 2026-03-24)

Enabled all three `@Disabled` test factories.

#### Root-level tests (77 files): 46 pass, 18 fail, 13 skip

#### FHIR R4 tests (16 files): 12 pass, 4 fail

#### FHIR R4.0.1 tests (28 files): 13 pass, 14 fail, 1 skip

#### Library Resolution Phase (2026-03-24)

Added `ModelIntegration` and `LibraryResolution` phases to SemanticAnalyzer pipeline.
Pre-compiles implicit helper libraries (e.g., FHIRHelpers) referenced by model conversion
info, so they are cached in LibraryManager for on-demand resolution during type inference.
Also resolves explicit includes from CQL source. Result: 20 previously-failing tests now pass
(EqualityWithConversions, TestChoiceTypes, TestImplicitFHIRHelpers, TestContext,
TestIntervalImplicitConversion, TestRetrieveWithConcept, etc.).

#### Remaining Failure Categories

| Category | Count | Tests | Issue |
|----------|-------|-------|-------|
| **FHIR emission gaps** | ~14 | TestFHIR, TestFHIRHelpers, TestFHIRTiming, TestFHIRWithHelpers, TestFHIRPath, etc. | Various FHIR property mapping, context parameter, medication request, terminology wrapping |
| **Error recovery** | ~5 | Issue616, IdentifierDoesNotResolve..., TestIdentifierCaseMismatch, TestIncorrectParameterType1204 | New pipeline preserves invalid expressions; legacy replaces with Null. |
| **Cross-library conversions** | ~2 | TupleDifferentKeys, UncertTuplesWithDiffNullFields | Legacy emits Null for cross-library function calls it can't resolve. |
| **Compiler options** | ~3 | TestCompatibilityLevel3, InTest, QuantityLiteralTest | Options not threaded through new pipeline. |
| **Minor emission diffs** | ~7 | TestComments, TestChoiceAssignment, TranslationTests, Issue587, Issue643, etc. | Mixed: model version emission, return clause diffs, ToDateTime promotion. |

### Implementation Status

| Component | Status |
|-----------|--------|
| SymbolCollector | Done |
| TypeResolver | Done (bottom-up + overload resolution + effective types) |
| ConversionPlanner | Done (all conversion kinds via ConversionTable) |
| ConversionTable | Done (5 conversion types, convergence loop) |
| ModelIntegration | Done (implicit helper library detection, version inference) |
| LibraryResolution | Done (explicit + implicit include pre-compilation) |
| SemanticValidator | Partial (identifiers, casts, recursive functions) |
| Lowering | Done (phrase expansion, interval operators, boundary selectors, coalescing, operator rewrites) |
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
| ConversionPlanner | `cql-to-elm/.../analysis/ConversionPlanner.kt` |
| ImplicitConversion / ConversionTable | `cql-to-elm/.../analysis/ImplicitConversion.kt` |
| SemanticValidator | `cql-to-elm/.../analysis/SemanticValidator.kt` |
| OperatorRegistry | `cql-to-elm/.../analysis/OperatorRegistry.kt` |
| Lowering | `cql-to-elm/.../analysis/Lowering.kt` |
| ModelIntegration | `cql-to-elm/.../analysis/ModelIntegration.kt` |
| LibraryResolution | `cql-to-elm/.../analysis/LibraryResolution.kt` |

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
