# Analysis Phases and Semantic Error Classification

How the analysis pipeline phases interact, their ordering constraints,
and the categories of semantic errors they detect.

---

## Phase Ordering

```
1. COLLECT   (SymbolCollector)
2. INFER     (TypeResolver — bottom-up type inference + overload resolution)
3. CONVERT   (ConversionInserter — AST rewrite with explicit conversions)
4. CHECK     (convergence test — if new conversions inserted, loop to INFER)
5. VALIDATE  (SemanticValidator — detect errors on final AST)
```

Steps 2–4 form the **INFER→CONVERT→CHECK convergence loop**. The loop
repeats (max 3 iterations) until CONVERT inserts zero new conversions.
After convergence, a final INFER pass produces the TypeTable used by
VALIDATE and codegen.

### Convergence loop detail

```
symbols = SymbolCollector.collect(library)

for iteration in 1..maxIterations:
    typeTable = TypeResolver(operatorRegistry).resolve(library, symbols)
    library   = ConversionInserter(typeTable, operatorRegistry).convertLibrary(library)
    if conversionsInserted == 0: break
    symbols   = SymbolCollector.collect(library)   // re-collect: AST changed

finalSymbols  = SymbolCollector.collect(library)
finalTypeTable = TypeResolver(operatorRegistry).resolve(library, finalSymbols)
semanticModel = SemanticModel(finalSymbols, finalTypeTable, ...)
SemanticValidator.validate(library, finalSymbols, semanticModel)
```

**Why re-collect SymbolTable?** ConversionInserter creates new AST nodes
(synthetic QueryExpressions for list conversions, ConversionExpression
wrappers, etc.). The SymbolTable holds references to expression
definitions — if those expressions were replaced by the CI, the
SymbolTable has stale references and the next TypeResolver pass would
traverse the old AST, missing CI-generated nodes.

**Why a convergence loop?** Inserting a conversion can change the type
of an expression, which may trigger additional conversions upstream.
Example: `Avg({1,2,3})` — the list conversion `List<Integer>` →
`List<Decimal>` changes the argument type, which the TypeResolver needs
to see to resolve the Avg overload and compute the return type as
Decimal.

### Ordering constraints

| Phase | Depends on | Why |
|-------|-----------|-----|
| COLLECT | Parse | Needs AST |
| INFER | COLLECT | Needs SymbolTable for identifier resolution |
| CONVERT | INFER | Needs TypeTable + OperatorResolutions to know what conversions to insert |
| CHECK | CONVERT | Needs to know if new conversions were inserted |
| VALIDATE | Final INFER | Needs final types to detect type errors accurately |

VALIDATE must run AFTER the convergence loop because:
- An expression might look like an error pre-conversion but be valid
  after conversion (e.g., `1 + 2.0` — Integer + Decimal doesn't match
  directly, but after conversion it's `ToDecimal(1) + 2.0` which is
  Decimal + Decimal)
- Conversely, a conversion might fail (no valid conversion path),
  which VALIDATE should catch

### What cannot be reordered

- COLLECT before INFER: type inference needs declarations
- INFER before CONVERT: conversions are determined by type inference
- CONVERT before VALIDATE: validation needs to see the final AST

### What could theoretically be merged

- INFER + CONVERT could be a single pass if the type resolver inserted
  conversions as it resolved them. But this makes the resolver more
  complex and mixes concerns. Keeping them separate is cleaner.

---

## Future: Side-Table IR (eliminates AST mutation)

The current ConversionInserter mutates the AST, which forces
SymbolTable re-collection each iteration and creates identity-tracking
issues (IdentityHashMap keys change when AST nodes are copied). A
side-table IR keeps the AST immutable:

```
AST (immutable)  +  SyntheticTable  =  IR
```

The `SyntheticTable` (`IdentityHashMap<Expression, List<Synthetic>>`)
stores conversion/wrapping directives keyed by expression identity.
TypeResolver consults it to compute "effective types" (source type +
applied synthetics):

```kotlin
override fun onFunctionCall(expr: ..., args: List<DataType?>): DataType? {
    val effectiveArgs = args.mapIndexed { i, type ->
        synthetics.effectiveType(expr, position = i, sourceType = type)
    }
    return resolveOverload(expr, effectiveArgs)
}
```

**The convergence loop stays** — type inference still depends on
conversion effects. What changes is the representation:

```
synthetics = empty
repeat {
    typeTable = TypeResolver(synthetics).resolve(library, symbols)
    newSynthetics = ConversionAnalyzer(typeTable).analyze(library)
    if (newSynthetics.isEmpty()) break
    synthetics.addAll(newSynthetics)
}
```

Benefits: AST stable → SymbolTable collected once → no identity bugs →
no idempotency guards → editor gets both source types and
post-conversion types. See `AST_DEVELOPMENT_PLAN.md` M19 for full
design.

---

## Semantic Error Classification

Errors detected by the translator, classified by which phase catches
them and their severity.

### Phase 1: COLLECT errors

| Error | Severity | Description |
|-------|----------|-------------|
| Duplicate definition | Error | Two definitions with the same name in the same scope |
| Unresolved model | Error | `using X` where model X cannot be loaded |
| Unresolved include | Error | `include X` where library X cannot be found |
| Invalid version | Error | Model/library version mismatch |

### Phase 2: INFER errors

| Error | Severity | Description |
|-------|----------|-------------|
| Unresolved identifier | Error | Identifier not found in any scope |
| Unresolved function | Error | Function name doesn't exist |
| Ambiguous overload | Error | Multiple operator overloads match with equal score |
| Circular reference | Error | Expression/function references itself (directly or indirectly) |
| No matching signature | Error | Function exists but no overload matches the argument types |
| Unresolved type | Error | Named type not found in model (e.g., `as FooBar`) |
| Unresolved property | Warning | Property name not found on type (needs model metadata) |
| No matching operator | Error | Binary/unary operator has no matching overload for operand types |

### Phase 3: CONVERT errors

| Error | Severity | Description |
|-------|----------|-------------|
| No valid conversion | Error | Operator resolution requires a conversion that doesn't exist |
| Incompatible cast | Error | `as` expression where source type can't be cast to target (e.g., List → scalar) |

### Phase 4: CHECK errors

| Error | Severity | Description |
|-------|----------|-------------|
| Non-convergence | Internal | Max iterations reached with conversions still being inserted |

### Phase 5: VALIDATE errors

| Error | Severity | Description |
|-------|----------|-------------|
| Invalid context reference | Error | Reference from specific context to unfiltered or vice versa |
| Name hiding | Warning | Identifier in inner scope shadows identifier in outer scope |
| Invalid sort expression | Error | Sort by expression that doesn't resolve to a comparable type |
| Invalid retrieve | Error | Retrieve on a type that doesn't exist in the model |
| Invalid code path | Error | Retrieve code path that doesn't exist on the type |
| Missing required from | Error | Query without `from` keyword (when `RequireFromKeyword` option is set) |
| Unused definition | Warning | Definition that is never referenced |
| Deprecated feature | Warning | Use of features deprecated in the current compatibility level |

### Cross-phase: Warnings

| Warning | Phase | Description |
|---------|-------|-------------|
| Name hiding | VALIDATE | Alias/let/operand shadows a definition |
| Implicit conversion applied | CONVERT | Integer promoted to Decimal, etc. (informational) |
| Narrow conversion | CONVERT | Potential precision loss (e.g., Decimal → Integer if it existed) |
| Duplicate model mapping | COLLECT | Target model URL collision |

---

## Phase interactions

### INFER ↔ COLLECT

Type inference needs the SymbolTable from COLLECT to:
- Resolve identifier expressions to definitions
- Look up function definitions for user-defined function calls
- Find terminology definitions (CodeSystem, ValueSet, etc.)
- Load model types via OperatorRegistry (which uses ModelManager)

No conflict — COLLECT is read-only after it runs (but must be re-run
when the AST changes in the convergence loop).

### INFER ↔ CONVERT (convergence loop)

INFER produces:
- `TypeTable[expression] = DataType` — the pre-conversion type
- `TypeTable.operatorResolution[expression] = OperatorResolution` — includes `conversions` list
- `TypeTable.identifierResolution[expression] = Resolution` — what the identifier means

CONVERT reads:
- The operator resolution to know WHAT conversions to insert WHERE
- The pre-conversion types to construct the right conversion nodes

CONVERT produces:
- A modified AST with explicit ConversionExpression/AsExpression nodes
- Synthetic QueryExpression nodes for list conversions
- Coalesce wrapping for CONCAT operands
- ChoiceType As-wrapping for if/case branches and union operands

CHECK tests convergence: if CONVERT inserted zero new nodes, the loop
terminates. Otherwise, SymbolTable is re-collected from the modified
AST and INFER runs again on the updated tree.

### VALIDATE ↔ everything

VALIDATE reads from:
- The final (converted) AST
- The final TypeTable (post-convergence)
- The SymbolTable (re-collected from final AST)
- Compiler options (compatibility level, etc.)

VALIDATE produces:
- Error/warning flags on AST expressions (in SemanticModel)
- Diagnostic messages (for reporting to the user)

Codegen reads:
- The converted AST (for structure)
- The final TypeTable (for resultType decoration)
- The error flags (for Null substitution)

---

## How this maps to the current code

### Implemented

| Phase | Code | Status |
|-------|------|--------|
| COLLECT | `SymbolCollector` | Done |
| INFER | `TypeResolver` + extension files | Done (bottom-up inference + overload resolution) |
| CONVERT | `ConversionInserter` | Done (operator/function/null/literal/case/interval/collection/ChoiceType conversions) |
| CHECK | Convergence loop in `SemanticAnalyzer` | Done (max 3 iterations, SymbolTable re-collection) |
| VALIDATE | `SemanticValidator` | Partial (unresolved identifiers, invalid casts, recursive functions) |
| EMIT | `ElmEmitter` + `EmissionContext` + 20 emission files | Done — mostly mechanical, remaining conversion logic being migrated |

### Not yet implemented

| Phase | Code | Status |
|-------|------|--------|
| VALIDATE: name hiding | — | Not started |
| VALIDATE: invalid context references | — | Not started |
| VALIDATE: invalid sort expressions | Partial (sort items skipped) |
| VALIDATE: retrieve validation | — | Not started |
| Side-table IR | — | Designed, not started (see M19 in AST_DEVELOPMENT_PLAN.md) |
