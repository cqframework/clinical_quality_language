# Analysis Phases and Semantic Error Classification

How the analysis pipeline phases interact, their ordering constraints,
and the categories of semantic errors they detect.

---

## Phase Ordering

```
1. COLLECT   (SymbolCollector)
2. INFER     (TypeResolver — bottom-up type inference + overload resolution)
3. CONVERT   (ConversionInserter — AST rewrite with explicit conversions)
4. RE-INFER  (TypeResolver on converted AST — verify convergence)
5. VALIDATE  (SemanticValidator — detect errors on final AST)
```

### Ordering constraints

| Phase | Depends on | Why |
|-------|-----------|-----|
| COLLECT | Parse | Needs AST |
| INFER | COLLECT | Needs SymbolTable for identifier resolution |
| CONVERT | INFER | Needs TypeTable + OperatorResolutions to know what conversions to insert |
| RE-INFER | CONVERT | Needs converted AST to verify post-conversion types |
| VALIDATE | RE-INFER | Needs final types to detect type errors accurately |

VALIDATE must run AFTER CONVERT because:
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
- RE-INFER might be skippable if we can prove convergence statically
  (CQL's conversion DAG doesn't cycle). But it's cheap insurance.

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

### Phase 4: RE-INFER errors

| Error | Severity | Description |
|-------|----------|-------------|
| Non-convergence | Internal | Conversions triggered new type conflicts (should not happen in CQL) |

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

No conflict — COLLECT is read-only after it runs.

### INFER ↔ CONVERT

INFER produces:
- `TypeTable[expression] = DataType` — the pre-conversion type
- `TypeTable.operatorResolution[expression] = OperatorResolution` — includes `conversions` list
- `TypeTable.identifierResolution[expression] = Resolution` — what the identifier means

CONVERT reads:
- The operator resolution to know WHAT conversions to insert WHERE
- The pre-conversion types to construct the right conversion nodes

CONVERT produces:
- A new AST with explicit ConversionExpression/AsExpression nodes
- The new AST replaces the original for all downstream phases

### CONVERT ↔ RE-INFER

RE-INFER runs on the converted AST. It should find:
- ConversionExpression nodes now have the correct post-conversion type
- Parent operators now see the correct operand types
- No new conversions are needed (convergence)

If RE-INFER finds new conversion requirements, that's a bug in CONVERT
(it missed something) or a pathological conversion chain (shouldn't
happen in CQL).

### VALIDATE ↔ everything

VALIDATE reads from:
- The final (converted) AST
- The final TypeTable (post-RE-INFER)
- The SymbolTable (unchanged since COLLECT)
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

### Currently implemented

| Phase | Code | Status |
|-------|------|--------|
| COLLECT | `SymbolCollector` | Done |
| INFER | `TypeResolver` + extension files | Done (bottom-up inference + overload resolution) |
| VALIDATE | `SemanticValidator` | Partial (unresolved identifiers, invalid casts, recursive functions) |
| EMIT | `ElmEmitter` + `EmissionContext` + 20 emission files | Done but contains conversion logic that should move to CONVERT |

### Not yet implemented

| Phase | Code | Status |
|-------|------|--------|
| CONVERT | `ConversionInserter` | Not started — conversion logic currently in emission |
| RE-INFER | Second pass of `TypeResolver` | Not started |
| VALIDATE: name hiding | — | Not started |
| VALIDATE: invalid context references | — | Not started |
| VALIDATE: invalid sort expressions | Partial (sort items skipped) |
| VALIDATE: retrieve validation | — | Not started |

### Migration path

1. Build `ConversionInserter` as `ExpressionFold<Expression>`
2. Move conversion logic from emission files into `ConversionInserter`
3. Wire into `SemanticAnalyzer` between INFER and VALIDATE
4. Simplify emission — remove conversion lookups
5. Add RE-INFER convergence check
6. Expand VALIDATE with remaining error categories
