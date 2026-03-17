# CQL AST Development Plan

This document describes the incremental plan for completing the CQL AST-based
compilation pipeline so that it produces fully typed ELM identical to the legacy
parse-tree visitor. It covers symbol table design, type inference, overload
resolution, ELM emitter completion, and the testing strategy for detecting
divergence between the two pipelines.

> **Status:** Proposed
> **Date:** 2026-03-13
> **Companion docs:** [AST Design](../cql/AST.md) ·
> [ADR 003](DECISIONS.md#adr-003-cql-ast-representation)

---

## Table of Contents

1. [Current State](#current-state)
2. [Architecture Overview](#architecture-overview)
3. [Design: Symbol Table](#design-symbol-table)
4. [Design: Type Inference](#design-type-inference)
5. [Design: Overload Resolution](#design-overload-resolution)
6. [Design: ELM Emitter](#design-elm-emitter)
7. [Incremental Milestones](#incremental-milestones)
8. [Testing Strategy](#testing-strategy)
9. [Acceptance Criteria](#acceptance-criteria)

---

## Current State

| Component | Status | Location |
|-----------|--------|----------|
| AST node types | **Complete** | `cql/src/commonMain/kotlin/org/hl7/cql/ast/` |
| AST Builder (parse tree → AST) | **Complete** | `cql/.../ast/Builder.kt` |
| AstWalker / Transformer | **Complete** | `cql/.../ast/AstWalker.kt`, `Transformer.kt` |
| CompilerFrontend | **Scaffolded** (empty) | `cql-to-elm/.../analysis/CompilerFrontend.kt` |
| SymbolCollector | **Placeholder** | Returns empty `SymbolTable` |
| TypeResolver | **Placeholder** | Returns library unchanged |
| SemanticValidator | **Placeholder** | Empty body |
| ElmEmitter | **Early** | Handles library header, usings, literals only |
| Parity test harness | **Working** | `ElmEmitterParityTest.kt` (one test case) |
| CQL type system (`org.hl7.cql.model`) | **Complete** | Shared between legacy and new paths |
| Legacy translator | **Complete** | `Cql2ElmVisitor` (~4,500 lines), `LibraryBuilder` (~3,500 lines) |

The gap is everything between "AST exists" and "ELM comes out fully typed."

---

## Architecture Overview

The new pipeline adds three semantic passes between the AST Builder and the ELM
Emitter. Each pass reads the immutable AST and populates or consumes **side
tables** rather than mutating nodes.

```
CQL Source + CqlCompilerOptions
  │
  ▼
┌──────────────────────────────────────────────────────┐
│                    PARSE                              │
│  (cql module)                                        │
│                                                      │
│  ANTLR Lexer/Parser → Parse Tree → AST Builder       │
│                                                      │
│  Output: CQL AST (immutable)                         │
└──────────────────────┬───────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────┐
│                   ANALYSIS                            │
│  (cql-to-elm/.../analysis/)                          │
│  Reads: semantic flags from CqlCompilerOptions       │
│                                                      │
│  Pass 1 ─ SymbolCollector                            │
│     Walks AST, populates SymbolTable                 │
│     (definitions, scopes, operator registry)         │
│                                                      │
│  Pass 2 ─ TypeResolver                               │
│     Walks AST + SymbolTable, populates TypeTable     │
│     (inferred types, resolved overloads,             │
│      implicit conversions)                           │
│     Respects: list promotion/demotion, interval      │
│      promotion/demotion, compatibility level          │
│                                                      │
│  Pass 3 ─ SemanticValidator                          │
│     Walks AST + SymbolTable + TypeTable              │
│     Emits diagnostics (errors, warnings)             │
│                                                      │
│  Output: AnalysisResult { AST, SymbolTable,          │
│          TypeTable, Diagnostics }                    │
└──────────────────────┬───────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────┐
│                    CODEGEN                            │
│  (cql-to-elm/.../codegen/)                           │
│                                                      │
│  ElmEmitter + EmissionContext                        │
│  Extension files: LiteralEmission, OperatorEmission, │
│    TemporalEmission, DefinitionEmission, ...         │
│                                                      │
│  Pure mechanical translation — no compiler options.  │
│  Output: ELM Library (structure only)                │
└──────────────────────┬───────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────┐
│                 POST-PROCESSING                       │
│  Reads: output flags from CqlCompilerOptions         │
│                                                      │
│  - ResultType annotator (EnableResultTypes)           │
│  - Locator annotator (EnableLocators)                │
│  - Annotation/narrative (EnableAnnotations)          │
│  - Signature annotator (signatureLevel)              │
│                                                      │
│  Output: ELM Library (annotated per options)         │
└──────────────────────┬───────────────────────────────┘
                       │
                       ▼
                  JSON / XML
```

### Guiding Principles

- **Four-phase pipeline.** The compiler is organized into `parse` (grammar
  and AST construction), `analysis` (symbol collection, type inference,
  semantic validation), `codegen` (mechanical ELM emission), and
  `post-processing` (output annotation per compiler options). Each phase
  has a dedicated package.
- **SemanticModel, not node mutation.** Analysis produces a `SemanticModel`
  (currently split across `SymbolTable` and `TypeTable`) keyed by AST node
  identity (`IdentityHashMap`). The AST stays immutable and serializable.
  The SemanticModel is the single artifact passed from analysis to codegen
  and post-processing.
- **AST fold over visitor.** The current `TypeResolver.inferType()` and
  `EmissionContext.emitExpression()` use manual `when` dispatch (~30 cases
  each). Adding a new AST node requires updating every dispatch. The
  target architecture is an **AST fold** (catamorphism) where the algebra
  is defined once per node type and the traversal is automatic. Kotlin's
  sealed class exhaustiveness checking ensures new nodes are handled at
  compile time. The fold eliminates boilerplate and makes the pipeline
  extensible without shotgun surgery.
- **Reuse existing type system.** The `org.hl7.cql.model` package (`DataType`,
  `ClassType`, `ListType`, etc.) is shared with the legacy compiler and is
  already correct.
- **Reuse existing model infrastructure.** `ModelManager`, `ModelInfoLoader`,
  and model info XML files are shared. No need to reimplement model loading.
- **Incremental, testable milestones.** Each milestone adds a language subset,
  and its acceptance criterion is JSON parity with the legacy translator on a
  defined set of CQL inputs.
- **Clean option separation.** Compiler options are classified into three
  categories; each is handled by the appropriate phase:

### Compiler Option Classification

Flags from `CqlCompilerOptions` belong to specific pipeline phases:

**Semantic flags → Analysis phase** (change CQL interpretation):

| Flag | Effect |
|------|--------|
| `DisableListPromotion` | Don't implicitly promote `T` → `List<T>` |
| `DisableListDemotion` | Don't implicitly demote `List<T>` → `T` |
| `EnableIntervalPromotion` | Allow implicit `T` → `Interval<T>` |
| `EnableIntervalDemotion` | Allow implicit `Interval<T>` → `T` |
| `DisableListTraversal` | Don't auto-traverse list-valued properties |
| `DisableMethodInvocation` | Don't resolve fluent-style function calls |
| `RequireFromKeyword` | Require `from` keyword in queries |
| `compatibilityLevel` | CQL version (1.3, 1.4, 1.5) |
| `validateUnits` | Validate UCUM units |
| `enableCqlOnly` | Restrict to CQL-only features |
| `EnableDateRangeOptimization` | Rewrite date-range filters on retrieves |

**Output flags → Post-processing phase** (change ELM shape):

| Flag | Effect |
|------|--------|
| `EnableResultTypes` | Add `resultTypeName`/`resultTypeSpecifier` |
| `EnableAnnotations` | Include source narrative annotations |
| `EnableLocators` | Include source location on ELM nodes |
| `signatureLevel` | `None`/`Differing`/`Overloads`/`All` |
| `EnableDetailedErrors` | Detailed error information |

**Orchestration flags → Pipeline control:**

| Flag | Effect |
|------|--------|
| `verifyOnly` | Parse and validate, don't emit ELM |
| `DisableDefaultModelInfoLoad` | Don't auto-load model info |
| `analyzeDataRequirements` | Run data requirements analysis |
| `collapseDataRequirements` | Collapse redundant requirements |
| `errorLevel` | Minimum severity to report |

---

## Design: Symbol Table

The `SymbolTable` replaces the legacy `LibraryBuilder`'s identifier stacks and
`CompiledLibrary` namespace. It is built in a single walk of the AST
(Pass 1 — `SymbolCollector`).

### Data Model

```kotlin
class SymbolTable(
    /** Top-level declarations keyed by name. */
    val declarations: Map<String, Declaration>,

    /** Scoped identifier map. Each AST node that opens a scope
        (Library, FunctionDefinition, QueryExpression, LetClauseItem)
        maps to the identifiers visible within it. */
    val scopes: Map<AstNode, Scope>,

    /** Operator registry loaded from System library + models. */
    val operatorRegistry: OperatorRegistry,

    /** Resolved library includes (alias → compiled library metadata). */
    val includes: Map<String, ResolvedInclude>,
)

sealed interface Declaration {
    val name: String
    val accessModifier: AccessModifier
    val node: AstNode            // back-reference for location
}

data class ExpressionDeclaration(…) : Declaration
data class FunctionDeclaration(
    …,
    val operands: List<OperandInfo>,
    val overloads: List<FunctionDeclaration>,  // same name, different signatures
) : Declaration
data class ParameterDeclaration(…, val defaultType: DataType?) : Declaration
data class CodeSystemDeclaration(…) : Declaration
data class ValueSetDeclaration(…) : Declaration
data class CodeDeclaration(…) : Declaration
data class ConceptDeclaration(…) : Declaration

data class Scope(
    val parent: Scope?,
    val identifiers: Map<String, IdentifierInfo>,
)

data class IdentifierInfo(
    val name: String,
    val type: DataType?,         // may be null before type resolution
    val kind: IdentifierKind,    // ALIAS, LET, OPERAND, PARAMETER, …
    val node: AstNode,
)
```

### Collection Algorithm (Pass 1)

`SymbolCollector` extends `AstWalker` and performs a single pre-order traversal:

1. **Library-level definitions** → register in `declarations`.
   - `UsingDefinition` → load model via `ModelManager`, register model types.
   - `IncludeDefinition` → resolve included library, register in `includes`.
   - `CodeSystemDefinition`, `ValueSetDefinition`, `CodeDefinition`,
     `ConceptDefinition` → register as terminology declarations.
   - `ParameterDefinition` → register with declared type (if present).
2. **Statements** → register expression and function definitions.
   - `FunctionDefinition` → group overloads by name.
   - `ContextDefinition` → record context stack state.
3. **Scope-opening nodes** → push new `Scope`.
   - `FunctionDefinition` → operands become identifiers.
   - `QueryExpression` → aliases, let bindings.
   - `WithClause` / `WithoutClause` → nested alias scope.
   - `AggregateClause` → accumulator identifier.
4. **Operator registry** → load System library operators via
   `SystemLibraryHelper` and model-provided conversions. This reuses the
   existing `OperatorMap` infrastructure.

**Error handling:** duplicate declarations, unresolved includes, and
unresolved models produce `Problem` entries in the diagnostics list rather
than throwing. The AST remains intact.

### IDE Support

The `SymbolTable` is the primary data structure for IDE features:

- **Go-to-definition:** look up identifier in `scopes`, follow to
  `Declaration.node.locator`.
- **Find references:** invert the scope map to find all `IdentifierExpression`
  nodes that resolve to a given declaration.
- **Autocomplete:** at a given cursor position, enumerate visible identifiers
  from the enclosing `Scope` chain plus `declarations`.
- **Rename:** find all references and rewrite identifiers via `Transformer`.

---

## Design: Type Inference

The `TypeTable` is built in Pass 2 (`TypeResolver`). It maps every
`Expression` node to its inferred `DataType`.

### Data Model

```kotlin
class TypeTable(
    /** Inferred result type for each expression node. */
    private val types: IdentityHashMap<Expression, DataType>,

    /** Resolved operator/function for each invocation node.
        Stores the selected overload and any implicit conversions
        applied to operands. */
    private val resolutions: IdentityHashMap<Expression, Resolution>,

    /** Implicit conversions inserted by the type resolver.
        Keyed by the operand expression that needs wrapping. */
    private val conversions: IdentityHashMap<Expression, Conversion>,
) {
    fun typeOf(expr: Expression): DataType?
    fun resolutionOf(expr: Expression): Resolution?
    fun conversionOf(expr: Expression): Conversion?
}

data class Resolution(
    val operator: Operator,
    val operandConversions: List<Conversion?>,  // parallel to operands
    val genericBindings: Map<TypeParameter, DataType>,
)
```

### Inference Algorithm (Pass 2)

`TypeResolver` extends `AstWalker` with a **post-order** traversal (children
are typed before parents). The algorithm mirrors the legacy `Cql2ElmVisitor`
but operates on the AST instead of the parse tree.

#### Literals

| AST Node | Inferred Type |
|----------|---------------|
| `IntLiteral` | `System.Integer` |
| `LongLiteral` | `System.Long` |
| `DecimalLiteral` | `System.Decimal` |
| `StringLiteral` | `System.String` |
| `BooleanLiteral` | `System.Boolean` |
| `NullLiteral` | `System.Any` |
| `DateTimeLiteral` | `System.DateTime` or `System.Date` (by component count) |
| `TimeLiteral` | `System.Time` |
| `QuantityLiteral` | `System.Quantity` |
| `RatioLiteral` | `System.Ratio` |
| `CodeLiteral` | `System.Code` |
| `ConceptLiteral` | `System.Concept` |
| `ListLiteral` | `List<T>` where `T` = common supertype of elements |
| `TupleLiteral` | `TupleType` with element names and inferred element types |
| `InstanceLiteral` | Resolved `ClassType` from model |
| `IntervalLiteral` | `Interval<T>` where `T` = common supertype of bounds |

#### Binary / Unary Operators

1. Infer operand types.
2. Build a `CallContext(operatorName, operandTypes)`.
3. Resolve via `OperatorRegistry` (see [Overload Resolution](#design-overload-resolution)).
4. Record `Resolution` in `TypeTable.resolutions`.
5. Record any operand conversions in `TypeTable.conversions`.
6. Set result type from resolved operator's return type.

#### Identifiers and Property Access

- `IdentifierExpression` → look up in `SymbolTable.scopes` chain.
  - If it resolves to an expression definition → use that definition's type
    (may require forward reference resolution; see below).
  - If it resolves to a parameter → use parameter's declared or default type.
  - If it resolves to a query alias → use the alias's source type.
- `PropertyAccessExpression` → resolve source type, then look up property
  in model type's element list.
- `IndexExpression` → resolve source as `List<T>`, result is `T`.

#### Function Calls

- `FunctionCallExpression` → resolve via `OperatorRegistry` using function
  name and argument types. Handles:
  - System library functions (built-in).
  - User-defined functions (from `SymbolTable.declarations`).
  - Included library functions (from `SymbolTable.includes`).
  - Fluent-style invocations (first argument is the implicit target).

#### Query Expressions

Query type inference follows the legacy algorithm:

1. **Sources** → each `AliasedQuerySource` gets the type of its source expression.
   For `RetrieveExpression`, the type comes from the model
   (`context type → model type`).
2. **Where / With / Without** → type is `Boolean`; no effect on result type.
3. **Let clauses** → infer type of each let expression, add to scope.
4. **Return clause** → if present, result type is `List<return-expression-type>`.
   If absent, result type depends on source count:
   - Single source → `List<source-type>`.
   - Multi-source → `List<TupleType>` with one element per alias.
5. **Sort clause** → does not affect result type.
6. **Aggregate clause** → result type is the accumulator expression type.
7. **Singular queries** (no alias) → unwrap the list.

#### Forward References

Expression definitions may reference other definitions declared later in the
file. The resolver handles this with a two-pass approach:

1. **First pass:** collect all definition signatures (name + declared type if
   any) during symbol collection.
2. **During type resolution:** if a forward reference is encountered and the
   target's type is not yet resolved, resolve the target first (depth-first).
   Circular references are detected by tracking an "in-progress" set and
   reported as errors.

#### Type Promotion and Demotion

When an operand type doesn't exactly match a signature, the resolver applies
implicit conversions following the priority order from the legacy
`ConversionMap`:

1. Exact match (score 0)
2. Subtype (score 1)
3. Compatible type (score 2)
4. Cast (score 3)
5. Simple conversion (score 4)
6. Complex conversion (score 5)
7. List promotion `T → List<T>` / demotion `List<T> → T` (score 6–9)
8. Interval promotion `T → Interval<T>` (score 7)

---

## Design: Overload Resolution

Overload resolution is the process of selecting the correct operator or
function definition when multiple candidates exist for a given call. The new
implementation reuses the existing `OperatorMap`, `Operator`, `Conversion`,
and `ConversionMap` classes from `org.cqframework.cql.cql2elm.model`.

### OperatorRegistry

```kotlin
class OperatorRegistry(
    /** System library operators (loaded once). */
    private val systemOperators: OperatorMap,

    /** Model-provided conversions. */
    private val conversionMap: ConversionMap,

    /** User-defined functions from current library + includes. */
    private val userFunctions: OperatorMap,
) {
    /**
     * Resolve an operator or function call.
     *
     * @param name      Operator or function name
     * @param operands  Inferred operand types
     * @param library   Optional library qualifier
     * @param options   Allow promotion/demotion, fluent, must-resolve flags
     * @return Resolution with selected operator and conversions, or null
     */
    fun resolve(
        name: String,
        operands: List<DataType>,
        library: String? = null,
        options: ResolutionOptions = ResolutionOptions.DEFAULT,
    ): Resolution?
}
```

### Resolution Algorithm

The algorithm is the same three-phase scoring used by the legacy
`OperatorMap.resolveOperator`:

**Phase 1 — Conversion scoring:**
For each candidate operator with matching name and arity, compute:
```
score = Σ ConversionMap.getConversionScore(actualType[i], formalType[i])
```
Discard candidates where any operand has no valid conversion.

**Phase 2 — Type precedence tiebreaker:**
If multiple candidates share the lowest conversion score, compute:
```
typeScore = Σ typePrecedence(formalType[i])
```
where precedence is: Simple(1) < Tuple(2) < Class(3) < Interval(4) <
List(5) < Choice(6).

**Phase 3 — Ambiguity:**
If still tied, report ambiguity error.

### Generic Instantiation

For operators with generic type parameters (e.g., `Equal<T>(T, T) → Boolean`):

1. Attempt to unify each actual operand type with the formal parameter type.
2. If unification succeeds for all parameters, instantiate the generic operator
   with the bound types.
3. Include the instantiated operator as a candidate in scoring.

This reuses `DataType.instantiate()` and `InstantiationContext` from the
existing type system.

### Method-Style Resolution

CQL supports fluent method syntax: `Patient.name.given.first()`. This is
resolved by:

1. Taking the first argument as the implicit target.
2. Prepending its type to the operand list.
3. Resolving as a normal function call.
4. If no match, try resolving without the implicit target (static call).

The `SystemMethodResolver` logic can be adapted for this.

---

## Design: ELM Emitter

The `ElmEmitter` converts the typed AST plus side tables into ELM. Once the
AST is fully typed, emission is a mostly mechanical translation.

### Architecture

```kotlin
class ElmEmitter(
    private val typeTable: TypeTable,
    private val symbolTable: SymbolTable,
    private val options: EmitterOptions = EmitterOptions(),
) {
    fun emit(library: AstLibrary): ElmLibrary
}

data class EmitterOptions(
    val includeAnnotations: Boolean = false,
    val includeResultTypes: Boolean = true,
    val includeLocators: Boolean = false,
    val signatureLevel: SignatureLevel = SignatureLevel.NONE,
)
```

### Emission Rules

The emitter walks the AST and for each node:

1. Creates the corresponding ELM node (using `IdObjectFactory` for ID
   assignment).
2. Sets `resultType` from `TypeTable.typeOf(node)`.
3. For operator/function invocations, uses `TypeTable.resolutionOf(node)` to
   select the correct ELM element type (e.g., `Add`, `FunctionRef`,
   `ExpressionRef`).
4. For operands with conversions in `TypeTable.conversions`, wraps the operand
   in the appropriate conversion ELM node (e.g., `ToInteger`, `As`,
   `FunctionRef` to a conversion function).
5. Sets `locator` if `options.includeLocators`.
6. Sets `annotation` if `options.includeAnnotations`.

### Key Mapping Rules

| AST Node | ELM Node |
|----------|----------|
| `OperatorBinaryExpression(ADD)` | `Add` |
| `OperatorBinaryExpression(EQUALS)` | `Equal` |
| `FunctionCallExpression` (system) | Specific ELM node (e.g., `First`, `Last`, `Count`) |
| `FunctionCallExpression` (user) | `FunctionRef` |
| `IdentifierExpression` → expression def | `ExpressionRef` |
| `IdentifierExpression` → parameter | `ParameterRef` |
| `IdentifierExpression` → alias | `AliasRef` |
| `PropertyAccessExpression` | `Property` |
| `QueryExpression` | `Query` with `AliasedQuerySource`, `LetClause`, etc. |
| `RetrieveExpression` | `Retrieve` |
| `IfExpression` | `If` |
| `CaseExpression` | `Case` |
| `IsExpression` | `Is` |
| `AsExpression` | `As` |
| `IntervalLiteral` | `Interval` |
| `ListLiteral` | `List` |
| `TupleLiteral` | `Tuple` |
| `InstanceLiteral` | `Instance` |

System function calls are mapped to their dedicated ELM elements rather than
generic `FunctionRef` nodes. This mapping follows the same pattern as
`SystemFunctionResolver` in the legacy compiler.

---

## Incremental Milestones

Each milestone is designed to be independently testable and mergeable. The
parity test suite grows with each milestone.

### Milestone 0: Infrastructure (Foundation)

**Goal:** Establish the testing harness and side-table plumbing so that
subsequent milestones can be developed and validated incrementally.

**Work items:**
- [ ] Define `TypeTable` and `Resolution` data structures.
- [ ] Expand `SymbolTable` beyond the current placeholder.
- [ ] Expand `CompilerFrontend.Result` to include `TypeTable` and diagnostics.
- [ ] Update `ElmEmitter` to accept `TypeTable` and `SymbolTable`.
- [ ] Build the parameterized parity test harness (see [Testing Strategy](#testing-strategy)).
- [ ] Add CI job that runs parity tests and reports coverage.

**Parity scope:** None yet (infrastructure only).

### Milestone 1: Literals and Definitions

**Goal:** Emit fully typed ELM for libraries containing only literal
expressions and basic definitions.

**Language subset:**
- Library header (name, version).
- `using System`
- `define` with literal expressions (all literal types).
- `parameter` definitions with default literal values.
- `context` definitions.
- Access modifiers (`public` / `private`).

**Work items:**
- [ ] `SymbolCollector`: collect expression definitions, parameter definitions,
  context definitions.
- [ ] `TypeResolver`: infer types for all literal node types.
- [ ] `ElmEmitter`: emit `ExpressionDef`, `ParameterDef`, `ContextDef` with
  result types.
- [ ] Emit `Literal`, `Null`, `Interval`, `List`, `Tuple`, `Instance`,
  `Quantity`, `Ratio`, `Code`, `Concept` ELM nodes.

**Parity test inputs:**
- `Simple.cql` (existing)
- New: `Literals.cql` covering all literal types
- `ParameterTest.cql`
- `DefaultContext.cql`

### Milestone 2: Arithmetic and Comparison Operators

**Goal:** Resolve and emit binary/unary operators with correct overload
resolution and type inference.

**Language subset (additions):**
- Binary operators: `+`, `-`, `*`, `/`, `mod`, `^`, `&`.
- Unary operators: negation, `not`, `successor`, `predecessor`.
- Comparison operators: `=`, `!=`, `~`, `!~`, `<`, `<=`, `>`, `>=`.

**Work items:**
- [ ] `OperatorRegistry`: load System library operators for arithmetic and
  comparison.
- [ ] `TypeResolver`: resolve binary/unary operators via `OperatorRegistry`.
- [ ] Handle implicit conversions (e.g., `Integer + Decimal` → promote
  `Integer` to `Decimal`).
- [ ] `ElmEmitter`: emit `Add`, `Subtract`, `Multiply`, `Divide`, `Modulo`,
  `Power`, `Concatenate`, `Negate`, `Not`, `Equal`, `NotEqual`, `Equivalent`,
  `Less`, `LessOrEqual`, `Greater`, `GreaterOrEqual`, etc.
- [ ] Emit conversion wrappers (`ToDecimal`, `ToString`, etc.) where needed.

**Parity test inputs:**
- `OperatorTests/ArithmeticOperators.cql`
- `OperatorTests/ComparisonOperators.cql`
- `OperatorTests/CqlComparisonOperators.cql`

### Milestone 3: Logical, Nullological, and String Operators

**Goal:** Complete the basic operator set.

**Language subset (additions):**
- Logical: `and`, `or`, `xor`, `implies`, `not`.
- Null handling: `is null`, `is not null`, `Coalesce`, `if null`.
- String: `+` (concatenation), `Length`, `Upper`, `Lower`, etc.

**Work items:**
- [ ] Load remaining system operators for logic, null, and string categories.
- [ ] `TypeResolver`: handle three-valued logic (nullable Boolean).
- [ ] `ElmEmitter`: emit `And`, `Or`, `Xor`, `Implies`, `IsNull`,
  `IsNotNull`, `Coalesce`, `If`, string function ELM nodes.

**Parity test inputs:**
- `OperatorTests/LogicalOperators.cql`
- `OperatorTests/NullologicalOperators.cql`
- `OperatorTests/StringOperators.cql`

### Milestone 4: Identifier Resolution and Expression References

**Goal:** Resolve identifiers to their definitions and emit `ExpressionRef`,
`ParameterRef`, `FunctionRef`.

**Language subset (additions):**
- References to other `define` statements.
- Forward references.
- `ParameterRef`.
- Function definitions and calls (non-overloaded initially).
- External function declarations.

**Work items:**
- [ ] `SymbolCollector`: build scope chains for function bodies.
- [ ] `TypeResolver`: resolve `IdentifierExpression` via scope lookup.
- [ ] Handle forward references with depth-first resolution and circular
  reference detection.
- [ ] `ElmEmitter`: emit `ExpressionRef`, `ParameterRef`, `FunctionRef`,
  `FunctionDef`, `OperandDef`.

**Parity test inputs:**
- `OperatorTests/Functions.cql`
- `OperatorTests/RecursiveFunctions.cql`
- `OperatorTests/ForwardReferences.cql`
- `LibraryTests/TestForwardDeclaration.cql`

### Milestone 5: Type Operators and Conversions

**Goal:** Handle `is`, `as`, `cast`, `convert`, and implicit conversions.

**Language subset (additions):**
- `is` / `as` / `cast` type operators.
- `convert` expressions.
- Implicit conversions (subtype, promotion, demotion).
- `Tuple` and `Class` type conversions.

**Work items:**
- [ ] `TypeResolver`: resolve type specifiers to `DataType` via model.
- [ ] Handle `is` (result type `Boolean`), `as` (result type is target type),
  `cast` (result type is target type, runtime check).
- [ ] `ElmEmitter`: emit `Is`, `As`, `Convert`, `CanConvert`, `ToBoolean`,
  `ToInteger`, `ToDecimal`, `ToString`, `ToDateTime`, `ToTime`, `ToQuantity`,
  `ToConcept`.

**Parity test inputs:**
- `OperatorTests/TypeOperators.cql`
- `OperatorTests/TupleAndClassConversions.cql`
- `OperatorTests/ImplicitConversions.cql`

### Milestone 6: Date/Time and Interval Operators

**Goal:** Temporal arithmetic and interval operations.

**Language subset (additions):**
- Date/time construction, component extraction.
- Duration/difference between.
- Interval constructors, `start of`, `end of`, `width of`, `point from`.
- Interval relationships: `contains`, `in`, `includes`, `included in`,
  `before`, `after`, `meets`, `overlaps`, `starts`, `ends`.
- Temporal phrases: `same or before`, `same or after`, `within N days of`, etc.
- `properly includes`, `properly included in`.
- `expand`, `collapse`.

**Work items:**
- [ ] Load System operators for date/time and interval categories.
- [ ] `TypeResolver`: infer temporal expression types, handle precision
  parameters.
- [ ] Handle interval phrase normalization
  (`IntervalRelationExpression` → ELM interval operators).
- [ ] `ElmEmitter`: emit `DateTime`, `Date`, `Time`, `DateTimeComponentFrom`,
  `DurationBetween`, `DifferenceBetween`, `Interval`, `Start`, `End`,
  `Width`, `PointFrom`, `Contains`, `In`, `Includes`, `IncludedIn`,
  `Before`, `After`, `Meets`, `MeetsBefore`, `MeetsAfter`, `Overlaps`,
  `OverlapsBefore`, `OverlapsAfter`, `Starts`, `Ends`, `ProperContains`,
  `ProperIn`, `ProperIncludes`, `ProperIncludedIn`, `Expand`, `Collapse`,
  etc.

**Parity test inputs:**
- `OperatorTests/DateTimeOperators.cql`
- `OperatorTests/TimeOperators.cql`
- `OperatorTests/IntervalOperators.cql`
- `OperatorTests/CqlIntervalOperators.cql`
- `OperatorTests/IntervalOperatorPhrases.cql`
- `OperatorTests/AgeOperators.cql`

### Milestone 7: Queries

**Goal:** Full query expression support.

**Language subset (additions):**
- Single-source queries with `where`.
- Multi-source queries.
- `let` clauses.
- `return` and `sort` clauses.
- `with` / `without` clauses.
- `aggregate` clause.
- Singular queries (`singleton from`).

**Work items:**
- [ ] `SymbolCollector`: build query scopes (aliases, lets, aggregate
  accumulators).
- [ ] `TypeResolver`: infer query source types, alias types, let types,
  return type. Handle multi-source tuple construction.
- [ ] `ElmEmitter`: emit `Query`, `AliasedQuerySource`, `LetClause`,
  `With`, `Without`, `ReturnClause`, `SortClause`, `SortByItem`,
  `AggregateClause`, `AliasRef`, `QueryLetRef`.

**Parity test inputs:**
- `OperatorTests/Query.cql`
- `OperatorTests/MultiSourceQuery.cql`
- `OperatorTests/Sorting.cql`
- `OperatorTests/Aggregate.cql`
- `OperatorTests/AggregateOperators.cql`

### Milestone 8: List Operators

**Goal:** List operations.

**Language subset (additions):**
- `exists`, `in`, `contains`.
- `union`, `intersect`, `except`.
- `distinct`, `flatten`, `First`, `Last`, `IndexOf`, `Count`, `Sum`, `Min`,
  `Max`, `Avg`, `Median`, `Mode`, `AllTrue`, `AnyTrue`.
- List promotion/demotion.

**Work items:**
- [ ] Load system operators for list category.
- [ ] `TypeResolver`: handle list element type inference, list
  promotion/demotion.
- [ ] `ElmEmitter`: emit `Exists`, `In`, `Contains`, `Union`, `Intersect`,
  `Except`, `Distinct`, `Flatten`, `First`, `Last`, `IndexOf`, `Count`,
  `Sum`, `Min`, `Max`, `Avg`, `Median`, `Mode`, `AllTrue`, `AnyTrue`,
  `SingletonFrom`, etc.

**Parity test inputs:**
- `OperatorTests/CqlListOperators.cql`
- `OperatorTests/ListOperators.cql`

### Milestone 9: FHIR Model Support

**Goal:** Support `using FHIR` and retrieve expressions against FHIR models.

**Language subset (additions):**
- `using FHIR version 'X.Y.Z'`
- Retrieve expressions: `[Condition]`, `[Encounter where status = 'active']`.
- FHIR type resolution and FHIRHelpers integration.
- Property access on FHIR types.
- FHIR-specific implicit conversions.

**Work items:**
- [ ] `ElmEmitter`: resolve model URIs for non-System models (FHIR, QDM, etc.).
- [ ] `SymbolCollector`: load FHIR model info, register types and conversions.
- [ ] `TypeResolver`: resolve `RetrieveExpression` source types from model,
  handle code filter paths.
- [ ] Handle FHIRHelpers implicit conversion functions.
- [ ] `ElmEmitter`: emit `Retrieve`, `Property` with full type information.

**Parity test inputs:**
- `fhir/r401/TestFHIR.cql`
- `fhir/r401/TestFHIRWithHelpers.cql`
- `fhir/r401/TestFHIRHelpers.cql`
- `fhir/r401/TestChoiceTypes.cql`
- `fhir/r401/TestContext.cql`

### Milestone 10: Library Includes and Terminology

**Goal:** Multi-library compilation and terminology references.

**Language subset (additions):**
- `include` statements with aliases.
- Qualified references to included library definitions.
- `codesystem`, `valueset`, `code`, `concept` definitions.
- `in` (value set membership), `in` (code system membership).
- Terminology-based retrieves.

**Work items:**
- [ ] `SymbolCollector`: resolve includes via `LibraryManager`, merge symbols.
- [ ] `TypeResolver`: resolve qualified references across libraries.
- [ ] `ElmEmitter`: emit `IncludeDef`, `CodeSystemDef`, `ValueSetDef`,
  `CodeDef`, `ConceptDef`, `CodeSystemRef`, `ValueSetRef`, `CodeRef`,
  `ConceptRef`, `InValueSet`, `InCodeSystem`, `AnyInValueSet`,
  `AnyInCodeSystem`.

**Parity test inputs:**
- `OperatorTests/TerminologyReferences.cql`
- `LibraryTests/BaseLibrary.cql` + `LibraryTests/ReferencingLibrary.cql`
- `LibraryTests/AccessModifierLibrary.cql`

### Milestone 11: Advanced Features and Full Coverage

**Goal:** Handle remaining language features and edge cases.

**Language subset (additions):**
- `Message` operator.
- Signature levels (overloaded functions with different signatures).
- Name hiding warnings.
- `case` expressions (with and without comparand).
- `if` expressions.
- Fluent function syntax.
- Generic function overloads.
- All remaining edge cases.

**Work items:**
- [ ] Handle `Message` ELM node.
- [ ] Support signature output levels (`None`, `Differing`, `Overloads`,
  `All`).
- [ ] Name hiding detection and warnings.
- [ ] Fluent method resolution.
- [ ] Generic overload resolution.

**Parity test inputs:**
- `OperatorTests/MessageOperators.cql`
- `SignatureTests/*.cql`
- `HidingTests/*.cql`
- `LibraryTests/TestFluent*.cql`
- All remaining test files.

### Milestones 12–18 (completed)

**M12:** Batch FullParityTest, initial parity fixes (9→10/32).
**M12b:** SemanticModel, IdentityHashMap, ExpressionFold catamorphism.
**M13:** Expand/collapse, type specifiers, identifier scoping (→11/32).
**M14:** Null-As wrapping, sort direction, function-to-ELM mapping.
**M15:** Type coercion grind — list/interval/choice wrapping (→19/32).
**M16:** Includes/contains disambiguation, interval expansion (→20/32).
**M17:** SemanticValidator, error recovery, SemanticAnalyzer rename,
catamorphism migration, AnalysisMetrics (→24/32).
**M18:** ConversionInserter — operator/function/null/literal/case/
interval/collection conversions moved from emission to analysis.
Remaining in emission: aggregate query wrapping, interval expansion,
concatenation Coalesce wrapping (synthetic ELM constructions).

### Milestone 19: Remaining Synthetic Constructions + IR Design

**Goal:** Handle the remaining synthetic ELM constructions and design
the IR step for long-term cleanliness.

**Remaining conversion logic in emission:**
- [ ] **Aggregate query wrapping** — synthesize `QueryExpression` in
  ConversionInserter for `Avg`/`Median`/etc. on integer lists.
  Document as legacy construction (LEGACY_ISSUES.md #14).
- [ ] **Concatenation Coalesce wrapping** — synthesize
  `FunctionCallExpression("Coalesce", ...)` in ConversionInserter.
  Document as legacy construction (LEGACY_ISSUES.md #16).
- [ ] **Interval type expansion** — synthesize interval reconstruction
  with Property extraction. Requires AST interval property access.
  Document as legacy construction (LEGACY_ISSUES.md #15).
- [ ] **Set operator remaining conversions** — list/interval cast
  conversions in ListOperatorEmission.kt.
- [ ] **Re-infer convergence loop** — wire INFER→CONVERT→CHECK with
  max iterations and AnalysisMetrics tracking.
- [ ] **Clean up EmissionContext** — remove remaining conversion
  helpers (`applyConversion`, `wrapConversion`, `wrapAsConversion`,
  `wrapListConversion`, `wrapIntervalConversion`, `wrapCoalesce`).

**IR design (future):**
The synthetic constructions (aggregate Query, Coalesce wrapping,
interval expansion, Skip/Take/Tail→Slice) show the need for an
Intermediate Representation between analyzed AST and ELM. The IR
would be "AST + explicit conversions + synthetic nodes" — keeping
the source AST pure and emission mechanical. For now, synthesizing
AST nodes is pragmatic; the IR is a future milestone.

### Milestone 20: Post-processing + Compiler Options

**Goal:** Implement the post-processing phase and full compiler
options support.

**Work items:**
- [ ] Post-processing phase: add serialized `resultTypeName` /
  `resultTypeSpecifier` when `EnableResultTypes` is on.
- [ ] Signature annotator (`signatureLevel`). Needs SemanticModel
  access for OperatorResolution.
- [ ] Locator annotator (`EnableLocators`).
- [ ] Annotation/narrative annotator (`EnableAnnotations`).
- [ ] Compatibility level gating (`isCompatibleWith`) for
  `ValueSetRef.preserve`, default context name, etc.
- [ ] `EnableDateRangeOptimization` as AST Transformer pass.

### Milestone 21: Cross-Library + Terminology

**Goal:** Multi-library compilation and terminology-based retrieves.

**Work items:**
- [ ] LibraryManager integration for include resolution.
- [ ] FHIRHelpers bootstrapping strategy.
- [ ] Qualified cross-library references (`"LibAlias"."DefName"`).
- [ ] Terminology-based retrieves (`[Condition: "Diabetes"]`).
- [ ] Property path collapsing and list traversal.

**Target:** Unblocks TerminologyReferences.cql, all LibraryTests.

### Milestone 22: FHIR Deep Integration + Full Parity Sweep

**Goal:** 100% parity across the entire test suite.

**Work items:**
- [ ] FHIR implicit conversions (`.value` accessor stripping).
- [ ] FHIR-specific property resolution and type mapping.
- [ ] Full 358-file parity suite.
- [ ] Pipeline selection option (`legacy` vs `ast`).
- [ ] Performance benchmarks.
- [ ] Deprecation plan for `Cql2ElmVisitor` path.

---

## Testing Strategy

### Parity Test Harness

The core testing mechanism is **JSON comparison** between the legacy translator
and the new AST pipeline, extending the existing `ElmEmitterParityTest`.

```kotlin
@TestFactory
fun parityTests(): Collection<DynamicTest> {
    return testCases.map { (name, cqlPath) ->
        DynamicTest.dynamicTest(name) {
            val cql = loadResource(cqlPath)

            // New pipeline
            val ast = Builder().parseLibrary(cql)
            val frontend = CompilerFrontend(modelManager, libraryManager)
            val result = frontend.analyze(ast.library)
            val emitted = ElmEmitter(result.typeTable, result.symbolTable)
                .emit(result.library).library

            // Legacy pipeline
            val legacy = CqlTranslator.fromText(cql, libraryManager)
                .toELM()

            // Compare normalized JSON
            val emittedJson = normalize(serialize(emitted))
            val legacyJson = normalize(serialize(legacy)))

            assertEquals(legacyJson, emittedJson,
                diffReport(legacyJson, emittedJson))
        }
    }
}
```

### Normalization Rules

The JSON normalizer strips fields that are expected to differ:
- `localId` — auto-generated IDs may differ in numbering.
- `locator` — source positions may differ slightly.
- `annotation` — annotations are pipeline-specific.

All other fields must match exactly, including:
- `resultTypeName` and `resultTypeSpecifier` — type inference must agree.
- Operator element types — overload resolution must select the same operator.
- Conversion wrappers — implicit conversions must be identical.

### Test Categories

| Category | Purpose | Input files |
|----------|---------|-------------|
| **Smoke tests** | Basic sanity per milestone | Hand-crafted minimal CQL |
| **Operator parity** | Every operator category | `OperatorTests/*.cql` (32 files) |
| **Library parity** | Multi-library, includes | `LibraryTests/*.cql` (52 files) |
| **FHIR parity** | Model-dependent features | `fhir/**/*.cql` (102 files) |
| **QICore parity** | Clinical quality measures | `qicore/**/*.cql` (42 files) |
| **Edge cases** | Error handling, hiding, etc. | `HidingTests/`, `SignatureTests/`, issue files |
| **Full suite** | All test files | All 358 CQL files |

### Progressive Test Enabling

Each milestone enables a new batch of test files. Tests are annotated so that
un-implemented features produce clear skip messages rather than failures:

```kotlin
private val testCases: List<TestCase> = buildList {
    // Milestone 1
    add("Simple literals" to "ast/Simple.cql")
    add("All literal types" to "ast/Literals.cql")

    // Milestone 2 (enabled when arithmetic is implemented)
    add("Arithmetic operators" to "OperatorTests/ArithmeticOperators.cql")
    // ...
}
```

Tests for future milestones can be marked with `@Disabled("Milestone N")` or
placed behind a feature flag so CI stays green while development progresses.

### Differential Reporting

When a parity test fails, the test should produce a human-readable diff
showing exactly which ELM nodes diverge. This can be achieved with:

1. **JSON path diffing:** walk both JSON trees, report paths where values
   differ (e.g., `$.library.statements.def[0].expression.type` expected `Add`
   got `Concatenate`).
2. **Side-by-side output:** print the normalized JSON for the specific
   divergent subtree.

### Unit Tests for Individual Passes

In addition to parity tests, each pass has focused unit tests:

- **SymbolCollector tests:** given AST, assert symbol table contents
  (declaration names, scopes, identifier kinds).
- **TypeResolver tests:** given AST + symbol table, assert inferred types
  for specific expressions.
- **OperatorRegistry tests:** given operand types, assert correct operator
  selection and conversion plan.
- **ElmEmitter tests:** given AST + type table + symbol table, assert
  specific ELM node structures.

---

## Acceptance Criteria

### Per-Milestone Acceptance

Each milestone is accepted when:

1. **Parity:** All CQL test inputs listed for the milestone produce identical
   normalized JSON from both pipelines.
2. **No regressions:** All tests from prior milestones continue to pass.
3. **Unit coverage:** Individual pass tests cover the new functionality.
4. **Diagnostics:** Errors in the new pipeline (unresolved identifiers, type
   mismatches, ambiguous overloads) produce `Problem` entries with accurate
   `Locator` information — not crashes.
5. **Multiplatform:** Code compiles and basic tests pass on JVM, JS, and WASM
   targets.

### Final Acceptance (Milestone 22)

The AST pipeline is considered complete when:

1. **Full parity:** All 358 CQL test files produce identical normalized ELM
   JSON (modulo `localId`, `locator`, `annotation`).
2. **Error parity:** Error cases produce equivalent diagnostics (same error
   codes/messages for invalid CQL).
3. **Performance:** The new pipeline is no more than 2x slower than the legacy
   pipeline on the full test suite (expected to be faster long-term due to
   simpler structure).
4. **IDE readiness:** `SymbolTable` supports go-to-definition, find-references,
   and autocomplete queries with sub-second latency on typical libraries.
5. **Documentation:** Design documents, inline documentation, and ADR
   updated to reflect final implementation.
6. **CI integration:** Parity tests run in CI on every PR. Any divergence
   breaks the build.

### Intentional Divergences

Some divergences from the legacy translator may be **intentional** (bug fixes
or improvements). These must be:

1. Documented in a `KNOWN_DIVERGENCES.md` file with rationale.
2. Approved by a PMC member.
3. Excluded from the normalization comparison via targeted rules (not blanket
   exclusions).

---

## Appendix: Key File Locations

The new pipeline is organized into four phases with corresponding packages:

| Phase | Package | Purpose |
|-------|---------|---------|
| **Parse** | `org.hl7.cql.ast` (in `cql` module) | Grammar, AST nodes, Builder |
| **Analysis** | `org.cqframework.cql.cql2elm.analysis` | SemanticAnalyzer, TypeResolver, ConversionInserter, SemanticValidator |
| **Codegen** | `org.cqframework.cql.cql2elm.codegen` | ElmEmitter, EmissionContext, *Emission.kt files |
| **Post-processing** | (TODO) | ResultType, locator, annotation, signature annotation |

**Analysis phase:**

| Component | Path |
|-----------|------|
| SemanticAnalyzer | `cql-to-elm/.../analysis/SemanticAnalyzer.kt` |
| SemanticModel | `cql-to-elm/.../analysis/SemanticModel.kt` |
| SemanticValidator | `cql-to-elm/.../analysis/SemanticValidator.kt` |
| AnalysisMetrics | `cql-to-elm/.../analysis/AnalysisMetrics.kt` |
| ConversionInserter | `cql-to-elm/.../analysis/ConversionInserter.kt` |
| TypeResolver (core) | `cql-to-elm/.../analysis/TypeResolver.kt` |
| Type operator inference | `cql-to-elm/.../analysis/TypeOperatorInference.kt` |
| Temporal type inference | `cql-to-elm/.../analysis/TemporalTypeInference.kt` |
| Query type inference | `cql-to-elm/.../analysis/QueryTypeInference.kt` |
| OperatorRegistry | `cql-to-elm/.../analysis/OperatorRegistry.kt` |
| OperatorNames | `cql-to-elm/.../analysis/OperatorNames.kt` |

**Codegen phase:**

| Component | Path |
|-----------|------|
| ElmEmitter | `cql-to-elm/.../codegen/ElmEmitter.kt` |
| EmissionContext | `cql-to-elm/.../codegen/EmissionContext.kt` |
| EmissionHelpers | `cql-to-elm/.../codegen/EmissionHelpers.kt` |
| Literal emission | `cql-to-elm/.../codegen/LiteralEmission.kt` |
| Temporal (literal) emission | `cql-to-elm/.../codegen/TemporalEmission.kt` |
| Operator emission | `cql-to-elm/.../codegen/OperatorEmission.kt` |
| Definition emission | `cql-to-elm/.../codegen/DefinitionEmission.kt` |
| Statement emission | `cql-to-elm/.../codegen/StatementEmission.kt` |
| Reference emission | `cql-to-elm/.../codegen/ReferenceEmission.kt` |
| Function emission | `cql-to-elm/.../codegen/FunctionEmission.kt` |
| System function emission | `cql-to-elm/.../codegen/SystemFunctionEmission.kt` |
| Type operator emission | `cql-to-elm/.../codegen/TypeOperatorEmission.kt` |
| Temporal operator emission | `cql-to-elm/.../codegen/TemporalOperatorEmission.kt` |
| Collection operator emission | `cql-to-elm/.../codegen/CollectionOperatorEmission.kt` |
| Interval operator emission | `cql-to-elm/.../codegen/IntervalOperatorEmission.kt` |
| List operator emission | `cql-to-elm/.../codegen/ListOperatorEmission.kt` |
| Query emission | `cql-to-elm/.../codegen/QueryEmission.kt` |
| Retrieve emission | `cql-to-elm/.../codegen/RetrieveEmission.kt` |
| Property access emission | `cql-to-elm/.../codegen/PropertyAccessEmission.kt` |
| Case emission | `cql-to-elm/.../codegen/CaseEmission.kt` |
| Terminology emission | `cql-to-elm/.../codegen/TerminologyEmission.kt` |

**Shared infrastructure:**

| Component | Path |
|-----------|------|
| AST nodes | `cql/src/commonMain/kotlin/org/hl7/cql/ast/` |
| AST Builder | `cql/.../ast/Builder.kt` |
| CQL type system | `cql/src/commonMain/kotlin/org/hl7/cql/model/` |
| Current translator | `cql-to-elm/.../Cql2ElmVisitor.kt` |
| LibraryBuilder | `cql-to-elm/.../LibraryBuilder.kt` |
| Operator model | `cql-to-elm/.../model/Operator.kt`, `OperatorMap.kt` |
| Conversion model | `cql-to-elm/.../model/Conversion.kt`, `ConversionMap.kt` |
| System operators | `cql-to-elm/.../model/SystemLibraryHelper.kt` |
| Model loading | `cql-to-elm/.../ModelManager.kt` |
| Compiler options | `cql-to-elm/.../CqlCompilerOptions.kt` |

**Tests and docs:**

| Component | Path |
|-----------|------|
| Parity tests (common) | `cql-to-elm/src/commonTest/.../codegen/ElmEmitterParityTest.kt` |
| FHIR parity tests (JVM) | `cql-to-elm/src/jvmTest/.../codegen/FhirParityTest.kt` |
| Full parity suite (JVM) | `cql-to-elm/src/jvmTest/.../codegen/FullParityTest.kt` |
| TypeResolver unit tests | `cql-to-elm/src/commonTest/.../analysis/TypeResolverTest.kt` |
| Test CQL files | `cql-to-elm/src/jvmTest/resources/org/cqframework/cql/cql2elm/` |
| Translator issues | `docs/LEGACY_ISSUES.md` |
| AST design doc | `cql/AST.md` |
| ADR log | `docs/DECISIONS.md` |
