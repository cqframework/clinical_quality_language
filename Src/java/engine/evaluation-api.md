# CQL Engine Evaluation API

`CqlEngine.evaluate()` allows you to execute CQL libraries and expressions/functions within those libraries.

The API comes with a DSL-style builder to configure the evaluation parameters. The overall structure of the `evaluate { ... }` block is as follows:

```kotlin
val evaluationResults = engine.evaluate {
    library(...) {
        expressions(...)
    }
}
```


## Multi-lib and multi-expression evaluation

The `evaluate { ... }` block can have multiple `library(...) { ... }` sub-blocks, which executes multiple libraries in a single evaluation call. `expressions(...)` can also be repeated inside library blocks, in which case all specified expressions/functions will be evaluated.


## Full-library evaluation

If `expressions(...)` is never executed within a library block or if `library(...)` doesn't have a block, all expressions in that library will be evaluated. You can mix and match full-library evaluations with specific expression evaluations in the same `evaluate { ... }` block.


## Evaluating specific expressions from a library

In the following example, only `expr1`, `expr2`, and `expr3` from `Lib1` will be evaluated:

```kotlin
library("Lib1") {
  expressions("expr1")
  expressions("expr2", EvaluationExpressionRef("expr3"))
}
```


## Running functions

The API supports evaluating functions (`define function func1(...): ...`) within CQL libraries with given parameters.

In the following example, `Lib1.func1` is called with parameters `1` and `2`:

```kotlin
library("Lib1") {
  expressions(EvaluationFunctionRef("func1", /*signature*/ null, /*args*/ listOf(1, 2)))
}
```

Function signature can be provided to disambiguate overloaded functions. It is optional if there is no ambiguity.


## Accessing evaluation results

`EvaluationResults` contains results for all evaluated libraries, grouped by library identifier. In a single-lib case, you can use `evaluationResults.onlyResultOrThrow` to get the single `EvaluationResult`.

`EvaluationResult` contains results for all evaluated expressions/functions within a library. The results can be accessed using the brackets operator `[...]` with the expression name or `EvaluationExpressionRef`/`EvaluationFunctionRef` as the key:

```kotlin
val evaluationResult = evaluationResults.onlyResultOrThrow
val exprValue = evaluationResult["expr1"]?.value
val funcValue = evaluationResult[evaluationFunctionRef]?.value
```

`EvaluationResult` also provides the `expressionResults` computed map of expression names to their results. Modifications to this map do not persist in the `EvaluationResult` instance.


## Full example

```kt
val evaluationResults = engine.evaluate {
    // Evaluates all expressions from the referenced library
    library(versionedIdentifier)

    // Repeat when multiple libraries need to be evaluated
    library("Library1") // Short for library(VersionedIdentifier().apply { id = "Library1" })

    // Evaluate specific expressions and functions.
    library("Library2") {
        // Use expressions(...) to pass in evaluation expression refs
        expressions(EvaluationExpressionRef("expr1"), EvaluationExpressionRef("expr2"), ...)

        // Same as above
        expressions("expr1", "expr2", ...)

        // Evaluate func1 with parameters (1, 2)
        expressions(EvaluationFunctionRef("func1", /*signature*/ null, /*args*/ listOf(1, 2)))
    }

    // Additional params
    contextParameter = "Patient" to pat1
    parameters = mapOf(...)
    debugMap = DebugMap()
    evaluationDateTime = ZonedDateTime.of(2025, 12, 31, 0, 0, 0, 0, ZoneOffset.UTC)
}

// Get results for library using versionedIdentifier
val libraryResults = evaluationResults.results[versionedIdentifier]
// Or use shorthand in the single-lib case (returns non-nullable EvaluationResult):
// val libraryResults = evaluationResults.onlyResultOrThrow

// Use the brackets operator [...] to get the results for expressions and
// function evaluations from EvaluationResult
val exprValue = libraryResults?["expr1"]?.value
val funcValue = libraryResults?[evaluationFunctionRef]?.value
```
