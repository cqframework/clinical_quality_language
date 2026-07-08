# v4 to v5 Migration

This page outlines the changes to the CQL engine from version 4 to version 5.

## Summary of changes

### CQL value representation

In v5, all non-null CQL values are represented in the engine as instances (implementors) of the sealed
`org.opencds.cqf.cql.engine.runtime.Value` interface. Kotlin primitives and interfaces (`kotlin.Boolean`, `kotlin.Int`,
`kotlin.collections.Iterable<Any?>`) and external classes (HAPI FHIR classes, etc.) are no longer used as CQL values.
This applies both to the engine core (evaluation visitor, evaluators) and its API (evaluation inputs and outputs).

Within the sealed CQL value class hierarchy, non-system structured types (e.g. instances of FHIR.Patient, FHIR.string,
etc.) are represented using the `org.opencds.cqf.cql.engine.runtime.ClassInstance` class. Informally, a class instance
is a structured value (a Tuple) with a `QName` type tag like `{http://hl7.org/fhir}Patient`.

See [CQL Value Types](cql-value-types.md) for the full list of CQL types and their corresponding classes.

### Changes to `ModelResolver` and `DataProvider`

All Java `Class<T>` references were replaced with ELM-native, multiplatform type specifiers and type `QName`s. The
`ModelResolver` interface was simplified to accommodate these changes:

* the `resolvePath()`, `as()`, `setValue()`, `objectEqual()`, `resolveType()` methods were removed,
* `is(value: Any?, type: Class<*>?): Boolean?` was changed to `is(valueType: String, type: QName): Boolean?`.

### `engine`'s new KMP project structure and build targets

CQL engine v5 supports both JVM and JS targets which use the same core engine logic in `commonMain`.
Platform-specific implementations of utilities like `LocalDateTime` are located in `jvmMain` and `jsMain`.

## The new CQL engine for JavaScript environments

Here is an example of using the JS engine to evaluate a CQL library:

```js
import { CqlEngine, Environment, EvaluationParams } from "@cqframework/cql/engine";

const environment = new Environment(libraryManager);
const engine = new CqlEngine(environment);

const exampleLibraryParamsBuilder = new EvaluationParams.LibraryParams.Builder();
// To evaluate specific expressions and not the entire Example library, use `exampleLibraryParamsBuilder.expressionsByName(...)` here
const exampleLibraryParams = libraryParamsBuilder.build();

const evaluationParamsBuilder = new EvaluationParams.Builder();
evaluationParamsBuilder.libraryByName("Example", exampleLibraryParams);
const evaluationParams = evaluationParamsBuilder.build();

const evaluationResults = engine.evaluate(evaluationParams);
```

A more complete example, including how to set up a web app that uses the JS engine, can be found in js/cql-to-elm-ui.
