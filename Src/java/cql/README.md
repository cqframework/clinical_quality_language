# CQL

This module contains the generated CQL parser and ModelInfo support. This is a Kotlin Multiplatform module supporting JVM, JS, and WASM targets.

## Parser

The CQL parser is generated from the ANTLR4 grammar located in the `../../grammar` directory. This module generates the parser via the `AntlrKotlin` build task, and includes the generated parser and lexer code. It also helper utilities for working with parse trees.

The parser is used by the [CQL-to-ELM translator](../cql-to-elm/README.md) to parse CQL source into a parse tree, which is then processed to produce ELM.

## Model Info

CQL requires model information to resolve types and their properties during parsing and compilation. These are provided to the compiler via a "module info" file. This module includes support for reading model info XML files and provides the built-in System model info (`cql/src/commonMain/resources/org/hl7/elm/r1/system-modelinfo.xml`). See data model references in the CQL specification: https://cql.hl7.org/07-physicalrepresentation.html#data-model-references.

Model infos can be generated from XML Schema (XSD) files using the `tools:xsd-to-modelinfo` module. Model infos are loaded via the `ModelInfoProvider` SPI, which allows custom model infos to be provided at runtime (see `cql/src/jvmMain/resources/META-INF/services/org.hl7.cql.model.ModelInfoProvider`).

On the JVM, model info XML files are typically loaded from the classpath. On JS and WASM, model info XML files are inlined at build time to avoid runtime file I/O.

Model infos are an implementation detail of the compiler, and not part of the normative spec.

CQL 2.0 introduces new constructs for defining models and types directly in CQL source. Support for these features is planned for a future release, and will likely supersede the existing model info mechanism.

## Model Types

The CQL module also includes Kotlin data classes representing the built-in CQL types defined in the CQL specification: https://cql.hl7.org/09-b-cqlreference.html#types-2. These types are used by the parser and compiler to represent literals and type references in CQL source. They are also used by the translator for type inference and function resolution.

## AST

There is ongoing work to introduce an explicit Abstract Syntax Tree (AST) representation of CQL source in this module. The AST will provide a more structured representation of CQL source than the parse tree, enabling better semantic analysis and tooling support. See [ADR 003](../docs/DECISIONS.md#adr-003-cql-ast-representation) and [AST](AST.md) for more details. This work is in progress and will be done in parallel in a source-compatible manner (i.e. a given CQL input will produce the same ELM output). The Java API may evolve, but any breaking changes will resolve in a major version of this project.


