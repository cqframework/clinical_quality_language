# Type Resolution Design

How the analysis phase resolves types, inserts conversions, and produces
a fully-typed AST for mechanical emission.

---

## The Loop

```
CQL Source
  │
  ▼
┌────────────────────────────────────────────────┐
│  PARSE                                         │
│  ANTLR → Parse Tree → AST Builder              │
│  Output: raw AST (immutable)                   │
└──────────────────┬─────────────────────────────┘
                   │
                   ▼
┌────────────────────────────────────────────────┐
│  COLLECT                                       │
│  SymbolCollector walks AST                     │
│  Output: SymbolTable (declarations, scopes)    │
└──────────────────┬─────────────────────────────┘
                   │
                   ▼
┌────────────────────────────────────────────────┐
│  INFER + UNIFY                                 │
│  TypeResolver walks AST + SymbolTable          │
│                                                │
│  For each expression (bottom-up):              │
│    1. Infer operand types                      │
│    2. Resolve operator/function overload        │
│       (scoring via OperatorRegistry)           │
│    3. Record:                                  │
│       - inferred type → TypeTable              │
│       - operator resolution → TypeTable        │
│       - needed conversions → TypeTable         │
│                                                │
│  Output: TypeTable (types, resolutions,        │
│          conversion requirements)              │
└──────────────────┬─────────────────────────────┘
                   │
                   ▼
┌────────────────────────────────────────────────┐
│  CONVERT (AST Transform)                       │
│  ConversionInserter walks AST + TypeTable      │
│  Uses ExpressionFold<Expression> (catamorphism │
│  where R = Expression, i.e., AST → AST)       │
│                                                │
│  For each expression:                          │
│    - If TypeTable says operand N needs          │
│      conversion C, wrap it:                    │
│      Add(int1, dec2)                           │
│        → Add(ToDecimal(int1), dec2)            │
│    - If TypeTable says null needs As(T),       │
│      wrap it:                                  │
│      Coalesce(expr, null)                      │
│        → Coalesce(expr, As(Integer, null))     │
│    - If list elements need promotion:          │
│      List(int1, dec2)                          │
│        → List(ToDecimal(int1), dec2)           │
│    - If aggregate needs query wrapping:        │
│      Avg(intList)                              │
│        → Avg(Query(intList, return ToDecimal)) │
│                                                │
│  Output: converted AST (new tree, same types)  │
│          All conversions are explicit nodes.    │
└──────────────────┬─────────────────────────────┘
                   │
                   ▼
┌────────────────────────────────────────────────┐
│  RE-INFER (conditional)                        │
│  TypeResolver walks converted AST              │
│                                                │
│  Why: inserted conversions change types.       │
│  ToDecimal(1) has type Decimal, not Integer.   │
│  This may affect parent operators.             │
│                                                │
│  In practice, CQL's type system is simple      │
│  enough that one conversion pass suffices —    │
│  conversions don't trigger new conversions.    │
│  But the architecture supports re-running.     │
│                                                │
│  Convergence check: if TypeTable is unchanged, │
│  stop. Otherwise, go back to CONVERT.          │
│                                                │
│  Output: final TypeTable (post-conversion)     │
└──────────────────┬─────────────────────────────┘
                   │
                   ▼
┌────────────────────────────────────────────────┐
│  VALIDATE                                      │
│  SemanticValidator walks converted AST +       │
│  final TypeTable                               │
│                                                │
│  Checks:                                       │
│    - Unresolved identifiers                    │
│    - Undeclared/unmatched function calls        │
│    - Recursive definitions                     │
│    - Invalid casts                             │
│    - Type mismatches that survived conversion  │
│                                                │
│  Records error flags in SemanticModel.         │
│  Output: SemanticModel (complete)              │
└──────────────────┬─────────────────────────────┘
                   │
                   ▼
┌────────────────────────────────────────────────┐
│  EMIT (purely mechanical)                      │
│  ElmEmitter walks converted AST + SemanticModel│
│                                                │
│  No conversion logic. No type coercion.        │
│  No operator resolution lookups.               │
│  Just 1:1 AST node → ELM node mapping.         │
│                                                │
│  ConversionExpression → ToDecimal/As/etc.      │
│  OperatorBinaryExpression → Add/Subtract/etc.  │
│  IfExpression → If                             │
│  ...                                           │
│                                                │
│  Error-flagged expressions → Null              │
│                                                │
│  Output: ELM Library (structure only)          │
└──────────────────┬─────────────────────────────┘
                   │
                   ▼
┌────────────────────────────────────────────────┐
│  POST-PROCESS (optional, per compiler options) │
│  - ResultType annotator                        │
│  - Locator annotator                           │
│  - Signature annotator                         │
│  - Annotation/narrative                        │
│  Output: ELM Library (annotated)               │
└────────────────────────────────────────────────┘
```

---

## What the ConversionInserter handles

### Operator conversions

When OperatorRegistry resolves `Add(Integer, Decimal)` and determines
the Integer operand needs `ToDecimal`:

```
Before:  Add(IntLiteral(1), DecimalLiteral(2.0))
After:   Add(ConversionExpression(IntLiteral(1), destType=Decimal),
             DecimalLiteral(2.0))
```

The conversion info comes from `TypeTable.getOperatorResolution(expr)` →
`OperatorResolution.conversions[0]`.

### Null-As wrapping

When a null literal is an operand to a typed operator:

```
Before:  Add(IntLiteral(1), NullLiteral)
After:   Add(IntLiteral(1), AsExpression(NullLiteral, type=Integer))
```

### List element promotion

When a list has mixed-type elements:

```
Before:  ListLiteral([IntLiteral(1), DecimalLiteral(2.0)])
After:   ListLiteral([ConversionExpression(IntLiteral(1), Decimal),
                      DecimalLiteral(2.0)])
```

### Interval bound promotion

```
Before:  IntervalLiteral(IntLiteral(1), DecimalLiteral(2.0))
After:   IntervalLiteral(ConversionExpression(IntLiteral(1), Decimal),
                         DecimalLiteral(2.0))
```

### If/Case branch promotion

When branches have different types that need a common supertype:

```
Before:  If(cond, IntLiteral(1), DecimalLiteral(2.0))
After:   If(cond, ConversionExpression(IntLiteral(1), Decimal),
              DecimalLiteral(2.0))
```

### Aggregate query wrapping

When `Avg(List<Integer>)` needs decimal promotion:

```
Before:  FunctionCall("Avg", [ListLiteral([1, 2, 3])])
After:   FunctionCall("Avg", [QueryExpression(
           sources=[ListLiteral([1, 2, 3])],
           return=ConversionExpression(AliasRef, Decimal)
         )])
```

### Function argument conversions

When a function expects `Decimal` but receives `Integer`:

```
Before:  FunctionCall("UserFunc", [IntLiteral(1)])
After:   FunctionCall("UserFunc",
           [ConversionExpression(IntLiteral(1), Decimal)])
```

---

## Why one pass usually suffices

CQL's implicit conversions form a DAG (not arbitrary):

```
Integer → Long → Decimal
Integer → Quantity
Decimal → Quantity
String → DateTime, Date, Time, Quantity, etc.
Code → Concept
```

No conversion produces a type that triggers another implicit conversion.
`Integer → Decimal` produces `Decimal`, which doesn't implicitly convert
to anything else in operator contexts. So inserting a `ToDecimal` node
doesn't create a new conversion requirement.

The exception is compound expressions like `Avg({1, 2, 3})` where the
aggregate wrapping inserts a Query with a ToDecimal return, which
changes the list type from `List<Integer>` to `List<Decimal>`. But the
Avg operator already expected `List<Decimal>`, so no further conversion
is needed.

A second pass (RE-INFER) should verify convergence but in practice
won't find new work.

---

## What changes from current architecture

### Moves OUT of emission (codegen)

- `lookupResolution()` / `applyAllConversions()` — no longer needed
- `applyConversion()` / `wrapAsConversion()` / `wrapIntervalConversion()`
- `wrapListConversion()` (implicit query wrapping)
- `wrapNullAs()` for null literal wrapping
- `applyImplicitConversion()` / `implicitConversionName()`
- All the null-wrapping logic scattered across operator emission files
- Choice type wrapping in if/case/union emission

### Moves INTO analysis

- `ConversionInserter` — new AST Transformer pass
- All of the above conversion logic, operating on AST nodes instead
  of ELM nodes

### Emission becomes

- `ConversionExpression` → appropriate ELM conversion node (ToDecimal,
  As, ToString, etc.) — already exists
- `AsExpression` → ELM As — already exists
- `OperatorBinaryExpression` → ELM Add/Subtract/etc. — already exists
- No special cases, no side-table lookups, no type-dependent branching

### SemanticModel carries

- SymbolTable (declarations)
- TypeTable (post-conversion types — accurate)
- Error flags
- Compiler options
- The converted AST (or the original AST if no conversions needed)

---

## Implementation plan

1. **Define ConversionInserter** as `ExpressionFold<Expression>` —
   the identity catamorphism with overrides for nodes that need
   conversion wrapping.

2. **Move conversion logic** from EmissionContext/emission files
   into ConversionInserter, adapting from ELM construction to AST
   construction (e.g., create `ConversionExpression` AST nodes
   instead of ELM `ToDecimal` nodes).

3. **Wire into SemanticAnalyzer** between TypeResolver and
   SemanticValidator.

4. **Simplify emission** — remove all conversion-related code from
   codegen. The `on*` handlers become pure 1:1 AST→ELM mapping.

5. **Add RE-INFER check** — run TypeResolver on the converted AST,
   verify no new conversions needed. Assert convergence.

6. **Verify parity** — all 24/32 tests must still pass.

This is incremental: start with the simplest conversions (operator
argument promotion), verify parity, then move more conversion logic
over until emission is purely mechanical.
