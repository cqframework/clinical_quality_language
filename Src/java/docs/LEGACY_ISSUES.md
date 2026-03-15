# Legacy Translator Issues

Issues discovered in the legacy `Cql2ElmVisitor`/`LibraryBuilder` translator
during development of the AST-based compilation pipeline.

---

## Bugs

### 1. `properly between` always behaves as plain `between`

**Location:** `Cql2ElmVisitor.kt:1403`
**Severity:** Bug — incorrect ELM output
**Status:** Fix submitted as PR #1704

`visitBetweenExpression` checks `ctx.getChild(0).text == "properly"` but
child 0 is always the expression per the grammar rule
`expression 'properly'? 'between' expressionTerm 'and' expressionTerm`.
The keyword is at child index 1 when present, so `isProper` is always
false. Both `between` and `properly between` emit
`And(GreaterOrEqual, LessOrEqual)` instead of `And(Greater, Less)` for
the proper variant.

### 2. `div` operator mapped to wrong ELM node

**Location:** AST `Builder.kt` (pre-existing, fixed in M12)
**Severity:** Bug — incorrect ELM output

The CQL `div` operator (truncated integer division) was mapped to
`DIVIDE` (same as `/`), producing a `Divide` ELM node instead of
`TruncatedDivide`. Fixed by adding `TRUNCATED_DIVIDE` to the
`BinaryOperator` enum.

### 3. `DateTimeOperators.cql` — legacy drops 70 of 71 statements

**Location:** `Cql2ElmVisitor.kt` / `LibraryBuilder.kt`
**Severity:** Bug — test file unusable

When compiling the `OperatorTests/DateTimeOperators.cql` file with only
the System model (no FHIR), the legacy translator silently drops most
expression definitions. Only 1 of 71 statements appears in the output.
This appears to be an internal resolution failure in `LibraryBuilder`
related to Date/DateTime type handling without a model context.

---

## Quirks (intentional or debatable behavior)

### 4. Null arguments to DateTime/Date/Time constructors wrapped in `As`

The legacy wraps null arguments to temporal constructors in explicit type
casts: `DateTime(null)` becomes `DateTime(As(Integer, null))`. The null
literal has no inherent type, so the legacy inserts `As` to satisfy the
constructor's expected parameter type. The new pipeline matches this
behavior for parity.

### 5. `Skip`/`Take`/`Tail` transformed to `Slice`

The legacy translator transforms these system functions:
- `Skip(list, n)` → `Slice(source=list, startIndex=n, endIndex=null)`
- `Take(list, n)` → `Slice(source=list, startIndex=0, endIndex=Coalesce(n, 0))`
- `Tail(list)` → `Slice(source=list, startIndex=1, endIndex=null)`

These are semantic rewrites, not simple function-to-ELM mappings. The
new pipeline matches this behavior for parity.

### 6. Aggregate accumulator resolves as `AliasRef`

In `aggregate` clauses, the accumulator variable (e.g., `acc` in
`aggregate acc starting 0: acc + X`) resolves as an `AliasRef` in the
ELM output, not a `QueryLetRef`. This is arguably a misnomer since the
accumulator is not a query alias, but the new pipeline matches it.

### 7. `ValueSetRef` always has `preserve = true`

Since CQL 1.5, `ValueSetRef` nodes include `preserve = true` to
indicate the reference should be treated as a `System.ValueSet` type
rather than being expanded to `List<Code>`. The legacy always sets this.

### 8. Implicit `using System` always emitted

Even when a CQL library has no explicit `using System` declaration, the
legacy translator always emits it in the ELM output. When a non-System
model is declared (e.g., FHIR), both `using System` and the model's
using definition appear.

### 9. Empty library identifier always emitted

The legacy always creates a `VersionedIdentifier` element in the ELM
output, even when the CQL has no `library` declaration. This results in
an empty `identifier: {}` in the JSON output.

### 10. Default context is `Unfiltered`

When no explicit `context` declaration appears in the CQL, the legacy
defaults all expression definitions to `context: "Unfiltered"`.

### 11. Model URI uses `targetUrl` over `url`

For models that define both `url` and `targetUrl` in their ModelInfo
(e.g., QICore has `url="http://hl7.org/fhir/us/qicore"` and
`targetUrl="http://hl7.org/fhir"`), the legacy uses `targetUrl` for
the UsingDef URI and Retrieve dataType QName namespace. The
`targetVersion` field similarly overrides `version` when present.

### 12. Quantity unit strings have quotes stripped

Quantity literal units in the AST include surrounding single quotes
(e.g., `'mg'`). The legacy strips these before emitting the ELM
Quantity node, producing `unit: "mg"` rather than `unit: "'mg'"`.

### 13. Integer literal text preserved for time components

Time literal components like `@T10:00:00` require the original text
(`"00"`) rather than the parsed integer value (`0`) to achieve parity.
The legacy preserves the source text representation. The new pipeline
added an optional `text` field to `IntLiteral` to support this.

---

## Error recovery differences

The legacy translator has error recovery behavior that the new pipeline
does not implement:

- **Invalid casts**: Legacy replaces invalid cast expressions with `Null`
- **Undeclared forward references**: Legacy replaces unresolved function
  references with `Null`
- **Undeclared signatures**: Legacy replaces unmatched function calls
  with `Null`
- **Recursive functions**: Legacy replaces recursive function bodies
  with `Null` (recursion is illegal in CQL)

The new pipeline either throws `UnsupportedNodeException` or returns
null types for these cases. Proper error recovery will be implemented
via the `SemanticValidator` pass.

---

## Type inference gaps

These are areas where the legacy performs more sophisticated type
inference that the new pipeline does not yet replicate:

### Null wrapping with `ListTypeSpecifier`

For list operators that receive null arguments, the legacy wraps null in
`As(ListTypeSpecifier(...))` to provide type context. For example,
`{1, 2} union null` becomes `Union({1, 2}, As(List<Integer>, null))`.
This requires inferring the expected list element type from context.

### Implicit aggregate query wrapping

For aggregate functions like `Avg` and `Median` applied to integer
lists, the legacy wraps the source in an implicit `Query` with a
`ToDecimal` conversion in the return clause. For example,
`Avg({1, 2, 3})` becomes `Avg(Query(source={1,2,3}, return ToDecimal(X)))`.
This ensures the aggregate operates on decimal values.

### Choice type union wrapping

For union/intersect operations on lists with different element types,
the legacy wraps operands in `As(List<Choice<T1, T2>>)` to create a
unified choice type. This requires computing choice types from the
operand types.
