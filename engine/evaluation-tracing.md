# Evaluation Tracing

[#1633](https://github.com/cqframework/clinical_quality_language/pull/1663) added support for recording and outputting evaluation traces. This is useful for debugging or understanding how specific results were derived.

An evaluation trace shows which expressions and functions were evaluated when `engine.evaluate()` was called, along with their inputs (function arguments) and outputs (intermediate evaluation results).

The trace is structured as a tree of frames, with each frame representing an evaluated expression or function, starting with the entrypoint expression or function call. When multiple entrypoint expressions are batch-executed in a single `engine.evaluate()` call, there are multiple top-level frames in the trace. Backtraces (included in `CqlException`s) use the same frame structure with the leaf frame representing the inner ELM node that failed to evaluate.

To enable evaluation tracing, add `EnableTracing` to engine options before calling `engine.evaluate()`. Tracing is disabled by default to avoid performance and memory overhead.

## Example

```kotlin
val engine = CqlEngine()

// Enable tracing
engine.state.engineOptions.add(CqlEngine.Options.EnableTracing)

// Evaluate the library
val result = engine.evaluate { library("Lib1") }.onlyResultOrThrow

// Print the evaluation trace
print(result.trace.toString())
```

Given the following CQL library:

```
library Lib1

define function func1(a Integer): a + 1

define expr1: 2

define function func2(b Integer): func1(b + 2) + func1(b + 3) + expr1

define expr2: func2(5)
```

the printed trace would be:

```
Lib1.expr1 = 2
Lib1.expr2 = 19
  Lib1.func2(b = 5) = 19
    Lib1.func1(a = 7) = 8
    Lib1.func1(a = 8) = 9
    Lib1.expr1 = 2
```

## Current limitations

* When caching is enabled and the same expression is evaluated multiple times, only the first evaluation appears in the trace. The trace frames are derived from activation frame stacks and only the first evaluation generates an activation frame.
* The formatting of the printed trace isn't too fancy at this stage.

## Next steps

- [ ] Remove `DebugResult`.