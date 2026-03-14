# CQL.js

![NPM Version](https://img.shields.io/npm/v/%40cqframework%2Fcql)

JavaScript tools for the Clinical Quality Language (CQL).

## Installation

    npm install @cqframework/cql

## Usage

`@cqframework/cql` is an [ESM](https://gist.github.com/sindresorhus/a39789f98801d908bbc7ff3ecc99d99c) package. You can
use it in Node.js and the browser (both on the main thread and in web workers).

### Compiling CQL to ELM

The library includes a fully featured CQL translator, allowing you to compile CQL to ELM in JavaScript environments:

```js
import { ModelManager, LibraryManager, CqlTranslator } from "@cqframework/cql/cql-to-elm";

const modelManager = new ModelManager();
// Register the necessary model info providers with the model manager here
const libraryManager = new LibraryManager(modelManager);
// Register the necessary library source providers with the library manager here
const cqlTranslator = CqlTranslator.fromText("library Test version '1.0.0'", libraryManager);
const elmJson = cqlTranslator.toJson();
```

### TypeScript support

The TypeScript definitions included in this package are automatically generated from the Kotlin code. The generator is
currently [experimental](https://kotlinlang.org/docs/js-project-setup.html#generation-of-typescript-declaration-files-d-ts),
so the types may not be perfect.

## Project status

This package is built from the Kotlin
Multiplatform [source](https://github.com/cqframework/clinical_quality_language/tree/main/Src/java) which itself
evolved from the reference Java implementation. The KMP project targets the JVM and JavaScript environments with
the goal of maintaining a single, shared codebase for both platforms.

This new JavaScript target is currently in beta and its API may change in future releases. If you encounter any issues,
please report them on [GitHub](https://github.com/cqframework/clinical_quality_language/issues)
or [Zulip](https://chat.fhir.org/#narrow/stream/179220-cql).
