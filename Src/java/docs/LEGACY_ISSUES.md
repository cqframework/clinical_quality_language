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

## Architecture notes

### Result type annotation should be post-processing, not codegen

The current new pipeline bakes `resultType` decoration into the
codegen phase via `EmissionContext.decorate()`, called from
`emitExpression()`. The translator implements this as a separate
post-processing step that can be toggled via options
(`DetailedReturnType` / `resultTypeName` / `resultTypeSpecifier`).

This is the better architecture: codegen should produce the ELM
structure mechanically, and a separate pass should annotate nodes with
type information based on compiler options. The new pipeline should
refactor `decorate()` out of `EmissionContext.emitExpression()` and
into a post-processing step.

### Compatibility level flags

The translator checks `isCompatibleWith("1.5")` (and other versions)
to gate behavior. CQL 1.3 and 1.4 libraries exist in the wild. The
new pipeline will need to accept a compatibility level parameter and
respect it for:

- `ValueSetRef.preserve` (1.5+ only)
- `Population` vs `Unfiltered` default context (compat level 3)
- Signature output levels
- Other version-dependent behaviors
