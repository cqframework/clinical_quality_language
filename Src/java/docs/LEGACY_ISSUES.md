# Translator Issues

Issues discovered in the `Cql2ElmVisitor`/`LibraryBuilder` translator
during development of the AST-based compilation pipeline.

---

## Bugs (fixed)

### 1. `properly between` always behaves as plain `between`

**Location:** `Cql2ElmVisitor.kt:1403`
**Status:** Fixed — PR #1704, merged to main

`visitBetweenExpression` checked `ctx.getChild(0).text == "properly"`
but child 0 is always the expression per the grammar rule. The keyword
is at child index 1 when present, so `isProper` was always false.

### 2. `div` operator mapped to wrong ELM node in AST Builder

**Location:** AST `Builder.kt`
**Status:** Fixed — PR #1705, merged to main

The CQL `div` operator (truncated integer division) was mapped to
`DIVIDE` (same as `/`). Fixed by adding `TRUNCATED_DIVIDE` to the
`BinaryOperator` enum.

### 3. Chunk-tracking error silently drops statements

**Location:** `Chunk.kt` / `CqlPreprocessorElmCommonVisitor.kt`
**Status:** Fixed — PR #1706, merged to main

When a CQL file has no `library` declaration and uses DateTime
constructors, `Chunk.addChunk()` threw `IllegalArgumentException`
because the child chunk's source interval fell outside the parent's
bounds. The exception was caught by the generic error handler, which
silently dropped the statement. Fix: expand the parent interval to
accommodate the child rather than failing.

---

## Translator behaviors with rationale

### 4. Null arguments to DateTime/Date/Time constructors wrapped in `As`

**Rationale:** Type safety for ELM consumers.

The null literal in CQL has no inherent type. DateTime/Date/Time
constructor parameters expect `Integer` (or `Decimal` for timezone
offset). The translator wraps null args in `As(Integer, null)` so the
ELM is fully typed for downstream consumers. This is a general pattern
in the translator: null operands to typed parameters get explicit casts.

### 5. `Skip`/`Take`/`Tail` transformed to `Slice`

**Rationale:** ELM normalization — `Slice` is the canonical ELM node.

`Skip`, `Take`, and `Tail` are CQL convenience functions that the ELM
spec represents as `Slice` (a more general operation). The translator
rewrites them:
- `Skip(list, n)` → `Slice(source=list, startIndex=n, endIndex=null)`
- `Take(list, n)` → `Slice(source=list, startIndex=0, endIndex=Coalesce(n, 0))`
- `Tail(list)` → `Slice(source=list, startIndex=1, endIndex=null)`

This is done in `SystemFunctionResolver` and is the intended mapping
per the ELM specification.

### 6. Aggregate accumulator resolves as `AliasRef`

**Rationale:** Implementation simplification.

The accumulator in an `aggregate` clause is pushed to the identifier
scope the same way query aliases are, producing `AliasRef` in ELM. A
dedicated `AggregateRef` node doesn't exist in the ELM schema, so
`AliasRef` is the closest match. The accumulator is scoped to the
aggregate expression body, same as a query alias is scoped to a query.

### 7. `ValueSetRef` has `preserve = true` for CQL 1.5+

**Rationale:** CQL 1.5 specification requirement.

CQL 1.5 introduced first-class `ValueSet` types. `preserve = true`
tells ELM consumers to treat the reference as a `System.ValueSet`
rather than expanding it to `List<Code>`. The translator checks
`isCompatibleWith("1.5")` before setting this flag. Note that CQL
1.3 and 1.4 libraries exist in the wild — the new pipeline will need
to respect the compatibility level and only set `preserve` for 1.5+.

### 8. Implicit `using System` always emitted

**Rationale:** Spec-compliant — the System model is implicitly
available.

The CQL spec (Developer's Guide) states: *"when the System model
declaration is implicit, it is not considered as part of determining
ambiguity"* — confirming the System model is always available without
an explicit `using System` declaration. The translator emits it in ELM
as a side-effect of `LibraryBuilder.beginTranslation()` loading the
System library's operators and types.

### 9. Empty library identifier always emitted

**Rationale:** Implementation artifact — not spec-required.

The CQL spec states: *"the library declaration is optional in a CQL
document, but if it is omitted, it is not possible to reference the
library from any other CQL library."* The `Library` ELM object is
created with an empty `identifier` field during `LibraryBuilder`
initialization. The ELM schema has `identifier` as optional, but the
translator always creates it.

### 10. Default context is `Unfiltered`

**Rationale:** CQL specification requirement.

The CQL spec (Author's Guide) states: *"When no context is specified
in the library, and the model has not declared a default context, the
default context is Unfiltered."* The translator initializes
`currentContext = "Unfiltered"` in `CqlPreprocessorElmCommonVisitor`.
(In CQL compatibility level 3, `Population` was the equivalent name.)

### 11. Model URI uses `targetUrl` over `url`

**Rationale:** FHIR profile mapping.

Models like QICore are FHIR profiles with their own URL
(`http://hl7.org/fhir/us/qicore`) but their types map to base FHIR
types (`http://hl7.org/fhir`). The `targetUrl` field in ModelInfo tells
the translator to use the base FHIR URL for ELM type QNames and
UsingDef URIs, so that ELM consumers can resolve types against the base
FHIR model. Applied in `LibraryBuilder.applyTargetModelMaps()`.

### 12. Quantity unit strings have quotes stripped

**Rationale:** UCUM convention — units are identifiers, not quoted
strings.

CQL grammar requires quotes around unit strings (`10 'mg'`), but UCUM
units in ELM are bare identifiers. The translator strips the enclosing
single quotes to produce the canonical UCUM representation.

### 13. Integer literal text preserved for time components

**Rationale:** Source fidelity in ELM literal values.

When the CQL source has `@T10:00:00`, the `00` components are parsed
as integer 0, but the ELM literal value should preserve `"00"` to
match the source representation. The translator emits the original
text rather than `Integer.toString()`. The new pipeline handles this
via an optional `text` field on `IntLiteral`.

---

## Error recovery differences

The translator has error recovery behavior that the new pipeline does
not yet implement:

- **Invalid casts**: Replaces invalid cast expressions with `Null`
- **Undeclared forward references**: Replaces unresolved function
  references with `Null`
- **Undeclared signatures**: Replaces unmatched function calls with
  `Null`
- **Recursive functions**: Replaces recursive function bodies with
  `Null` (recursion is illegal in CQL)

The new pipeline either throws `UnsupportedNodeException` or returns
null types for these cases. Proper error recovery will be implemented
via the `SemanticValidator` pass.

---

## Type inference gaps

Areas where the translator performs more sophisticated type inference
that the new pipeline does not yet replicate:

### Null wrapping with `ListTypeSpecifier`

For list operators that receive null arguments, the translator wraps
null in `As(ListTypeSpecifier(...))` to provide type context. For
example, `{1, 2} union null` becomes
`Union({1, 2}, As(List<Integer>, null))`. This requires inferring the
expected list element type from context.

### Implicit aggregate query wrapping

For aggregate functions like `Avg` and `Median` applied to integer
lists, the translator wraps the source in an implicit `Query` with a
`ToDecimal` conversion in the return clause. For example,
`Avg({1, 2, 3})` becomes
`Avg(Query(source={1,2,3}, return ToDecimal(X)))`. This ensures the
aggregate operates on decimal values per the spec (these aggregates
are defined over decimal).

### Choice type union wrapping

For union/intersect operations on lists with different element types,
the translator wraps operands in `As(List<Choice<T1, T2>>)` to create
a unified choice type. This requires computing choice types from the
operand types.

---

## Synthetic ELM constructions

Cases where the translator creates ELM structures that don't
correspond 1:1 to anything in the CQL source. All are spec-correct
semantic transformations required by CQL's type system, operator
definitions, or language semantics.

These constructions are the primary motivation for introducing an IR
between the AST and ELM (see "Need for an IR" below).

### 14. Aggregate query wrapping for numeric promotion

**Rationale:** `Avg`, `Median`, `StdDev`, `Variance` are defined only
over `List<Decimal>` and `List<Quantity>` in the System library.
`List<Integer>` is promoted via a synthetic Query with element-level
`ToDecimal` conversion.

```
Avg({1, 2, 3})
  → Avg(Query(source=[{1,2,3} as "X"], return=Return(ToDecimal(AliasRef("X")))))
```

### 15. Interval type expansion

When `Interval<Any>` needs to match `Interval<Integer>`, ELM has no
"cast interval" operator. The translator expands by extracting bounds
as properties and wrapping in `As`:

```
Interval(null, null)  -- Interval<Any>
  → Interval(
      low = As(Integer, Property(source=expr, path="low")),
      high = As(Integer, Property(source=expr, path="high")),
      lowClosed = Property(source=expr, path="lowClosed"),
      highClosed = Property(source=expr, path="highClosed"))
```

### 16. Concatenation Coalesce wrapping

CQL spec: `&` treats null as empty string. `+` propagates null.
Implemented by wrapping each operand in `Coalesce`:

```
'foo' & 'bar'
  → Concatenate(Coalesce('foo', ''), Coalesce('bar', ''))
```

### 17. Skip/Take/Tail → Slice

CQL convenience functions rewritten to the canonical ELM `Slice`
operator (see item 5 for details).

### 18. Timing offset arithmetic

`A starts 3 days before B` is a complex rewrite: extract Start/End
from intervals, apply arithmetic, compose into a comparison:

```
A starts 3 days before start B
  → SameAs(Start(A), Subtract(Start(B), Quantity(3, 'day')))
```

Variations with `or more`/`or less`/`more than`/`less than` qualifiers
produce `In(point, Interval[...])` constructions with synthetic
interval bounds computed from arithmetic.

### 19. Within interval construction

`A within 3 days of B` constructs a synthetic interval by applying
arithmetic to the right operand's bounds:

```
A within 3 days of B
  → In(A, Interval[Subtract(Start(B), Quantity(3, 'day')),
                    Add(End(B), Quantity(3, 'day'))])
```

### 20. Context definition implicit retrieve

`context Patient` synthesizes an expression definition:

```
context Patient
  → define Patient: SingletonFrom([Patient])
```

The `Retrieve` and `SingletonFrom` don't exist in the source.

### 21. CalculateAge from birth date

`AgeInYears()` resolves the patient's birth date property from the
model and synthesizes:

```
AgeInYears()
  → CalculateAge(Property(Patient, "birthDate"), Today(), years)
```

The Property access and Today() call are synthesized.

### 22. Between rewrite

`X between Y and Z` is rewritten based on the input type:

```
-- scalar:
5 between 1 and 10
  → And(GreaterOrEqual(5, 1), LessOrEqual(5, 10))

-- interval:
Interval[1,5] between Interval[1,3] and Interval[3,5]
  → IncludedIn(Interval[1,5], Interval[Interval[1,3], Interval[3,5]])
```

### 23. Point-interval promotion

When a point value is compared to an interval, the point is promoted
to a degenerate interval:

```
5 before Interval[6, 10]
  → If(IsNull(5), Null, Interval[5, 5]) before Interval[6, 10]
```

### 24. Convert unit

`convert X to 'unit'` synthesizes a Quantity literal for the unit:

```
convert 1 to 'mg'
  → ConvertQuantity(1, Quantity(1, 'mg'))
```

### Need for an IR

All of the above are **spec-correct semantic transformations**. The
compiler must express them in ELM even though they don't exist in the
CQL source. Currently they're split between the ConversionInserter
(which synthesizes AST nodes) and the emitter (which constructs ELM
directly).

A cleaner architecture: a **side-table IR** that keeps the AST immutable:

```
CQL Source → AST (pure source) → IR (AST + ConversionTable) → ELM
```

The IR is not a new tree. It's the original AST plus a
`ConversionTable` (`IdentityHashMap<Expression, List<ImplicitConversion>>`)
describing transformations to apply during emission. The AST never
changes — SymbolTable collected once, identity stable, no idempotency
guards needed.

The convergence loop stays (type inference depends on conversion
effects — e.g., `Avg({1,2,3})` needs to know `List<Integer>` converts
to `List<Decimal>` to resolve the overload). What changes is the
representation: `TypeResolver` reads "effective types" from the
ConversionTable instead of re-inferring on a mutated AST.

See `AST_DEVELOPMENT_PLAN.md` Milestone 19 for the full design
including the `ImplicitConversion` sealed hierarchy, convergence loop, and
migration path.

---

## Architecture notes

### Compiler flag classification

The existing `CqlCompilerOptions` has flags that fall into two distinct
categories. The new pipeline should separate these cleanly:

#### Semantic flags (affect analysis / CQL interpretation)

These change how CQL is interpreted and what ELM is produced. They
belong in the analysis phase (TypeResolver / SemanticValidator):

| Flag | Effect |
|------|--------|
| `DisableListPromotion` | Don't implicitly promote `T` → `List<T>` |
| `DisableListDemotion` | Don't implicitly demote `List<T>` → `T` |
| `EnableIntervalPromotion` | Allow implicit `T` → `Interval<T>` |
| `EnableIntervalDemotion` | Allow implicit `Interval<T>` → `T` |
| `DisableListTraversal` | Don't auto-traverse list-valued properties |
| `DisableMethodInvocation` | Don't resolve fluent-style function calls |
| `RequireFromKeyword` | Require `from` in queries (CQL syntax variant) |
| `compatibilityLevel` | CQL version (1.3, 1.4, 1.5) — gates `ValueSetRef.preserve`, default context name, etc. |
| `validateUnits` | Whether to validate UCUM units |
| `enableCqlOnly` | Restrict to CQL-only features (no FHIRPath extensions) |
| `EnableDateRangeOptimization` | Rewrite date-range filters on retrieves |

#### Output flags (affect ELM shape / post-processing)

These change how the ELM is annotated or serialized, without changing
its semantic content. They belong in a post-processing step, not in
codegen:

| Flag | Effect |
|------|--------|
| `EnableResultTypes` | Add `resultTypeName`/`resultTypeSpecifier` to ELM nodes |
| `EnableAnnotations` | Include source narrative annotations |
| `EnableLocators` | Include source location (`locator`) on ELM nodes |
| `signatureLevel` | `None`/`Differing`/`Overloads`/`All` — controls `signature` on operator invocations |
| `EnableDetailedErrors` | Include detailed error information |

#### Orchestration flags (affect the compilation pipeline itself)

| Flag | Effect |
|------|--------|
| `verifyOnly` | Parse and validate but don't emit ELM |
| `DisableDefaultModelInfoLoad` | Don't auto-load model info from classpath |
| `analyzeDataRequirements` | Run data requirements analysis pass |
| `collapseDataRequirements` | Collapse redundant data requirements |
| `errorLevel` | Minimum severity to report (`Info`/`Warning`/`Error`) |

### Current pipeline concern

The new pipeline currently bakes `resultType` decoration into the
codegen phase via `EmissionContext.decorate()`, called from
`emitExpression()`. The translator implements this as a separate
post-processing step that can be toggled.

The right architecture: codegen produces the ELM structure
mechanically, then a separate pass annotates nodes with type
information, locators, annotations, and signatures based on the
output flags. This keeps codegen simple and makes output options
composable.
