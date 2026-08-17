# v3 to v4 Migration

The [#1462](https://github.com/cqframework/clinical_quality_language/pull/1462) pull request converted the `cql-to-elm` Java project and its dependencies to Kotlin Multiplatform (KMP) targeting the JVM and JavaScript. The Java translator maintains feature parity with the previous version (see the summary of changes below), while the new JS target makes it possible to convert CQL to ELM in the browser and Node.js.


## Summary of changes for Java users

The Java API of the translator remains largely the same, however some changes have been made to accommodate KMP conventions.

### Dependency management
* Starting with v4, the releases will be published under the new `org.cqframework` group ID on Maven Central.
* Previously, you needed to add `elm-(jackson|jaxb)` and `model-(jackson|jaxb)` as extra dependencies to your project. This is no longer required because the default multiplatform serialization functionality is now included with `cql-to-elm`. The `elm-(jackson|jaxb)` and `model-(jackson|jaxb)` projects have been removed.
* For [`ucum-java`](https://github.com/FHIR/Ucum-java)-based unit validation to work in the translator on the JVM, you now need to add `ucum` as a dependency (a new project in this repository). Alternatively, you can provide your own implementation of the [`UcumService`](https://github.com/cqframework/clinical_quality_language/blob/master/Src/java/cql-to-elm/src/commonMain/kotlin/org/cqframework/cql/cql2elm/ucum/UcumService.kt) interface.

### Class and interface relocation, removals, and other API changes

Notable changes, grouped by the affected class or interface:

* [`CqlTranslator`](https://github.com/cqframework/clinical_quality_language/blob/master/Src/java/cql-to-elm/src/commonMain/kotlin/org/cqframework/cql/cql2elm/CqlTranslator.kt):
  * The `fromStream()` static method (all overloads) is replaced with `fromSource()` which accepts [`Source`](https://kotlinlang.org/api/kotlinx-io/kotlinx-io-core/kotlinx.io/-source/) instead of Java `InputStream`.
  * The `fromFile()` static method overloads now accept KMP [`Path`](https://kotlinlang.org/api/kotlinx-io/kotlinx-io-core/kotlinx.io.files/-path/) instead of Java `File`.
  * The `toXml()`, `toJson()`, `convertToXml()`, `convertToJson()` methods have new overloads that additionally accept an implementation of [`ElmLibraryWriterProvider`](https://github.com/cqframework/clinical_quality_language/blob/master/Src/java/elm/src/commonMain/kotlin/org/cqframework/cql/elm/serializing/ElmLibraryWriterProvider.kt), allowing you to provide a custom ELM serializer.
* [`LibraryManager`](https://github.com/cqframework/clinical_quality_language/blob/master/Src/java/cql-to-elm/src/commonMain/kotlin/org/cqframework/cql/cql2elm/LibraryManager.kt):
  * A new constructor overload additionally accepts an implementation of [`ElmLibraryReaderProvider`](https://github.com/cqframework/clinical_quality_language/blob/master/Src/java/elm/src/commonMain/kotlin/org/cqframework/cql/elm/serializing/ElmLibraryReaderProvider.kt), allowing you to provide a custom ELM deserializer.
* [`LibrarySourceProvider`](https://github.com/cqframework/clinical_quality_language/blob/master/Src/java/cql-to-elm/src/commonMain/kotlin/org/cqframework/cql/cql2elm/LibrarySourceProvider.kt):
  * The `getLibrarySource()` and `getLibraryContent()` methods now return [`Source`](https://kotlinlang.org/api/kotlinx-io/kotlinx-io-core/kotlinx.io/-source/) instead of Java `InputStream`.
* [`DefaultLibrarySourceProvider`](https://github.com/cqframework/clinical_quality_language/blob/master/Src/java/cql-to-elm/src/commonMain/kotlin/org/cqframework/cql/cql2elm/DefaultLibrarySourceProvider.kt):
  * The constructor accepts KMP [`Path`](https://kotlinlang.org/api/kotlinx-io/kotlinx-io-core/kotlinx.io.files/-path/) instead of Java `Path`.
* [`ElmLibraryWriter`](https://github.com/cqframework/clinical_quality_language/blob/master/Src/java/elm/src/commonMain/kotlin/org/cqframework/cql/elm/serializing/ElmLibraryWriter.kt):
  * The `write()` method accepts [`Sink`](https://kotlinlang.org/api/kotlinx-io/kotlinx-io-core/kotlinx.io/-sink/) instead of Java `Writer`.
* [`ElmLibraryReader`](https://github.com/cqframework/clinical_quality_language/blob/master/Src/java/elm/src/commonMain/kotlin/org/cqframework/cql/elm/serializing/ElmLibraryReader.kt):
  * The `read()` overloads accept `String` or [`Source`](https://kotlinlang.org/api/kotlinx-io/kotlinx-io-core/kotlinx.io/-source/).
* The `org.cqframework.cql.elm.tracking.(Trackable|TrackBack)` classes are moved to `org.cqframework.cql.cql2elm.tracking.(Trackable|TrackBack)`.
* `ModelInfoReader`, `ModelInfoReaderFactory`, `ModelInfoReaderProvider` are all replaced with a single multiplatform [`parseModelInfoXml()`](https://github.com/cqframework/clinical_quality_language/blob/master/Src/java/cql/src/commonMain/kotlin/org/hl7/elm_modelinfo/r1/serializing/XmlModelInfoReader.kt) method.
* The `CqlTranslatorOptionsMapper.fromFile()` static method is replaced with [`CqlTranslatorOptions.fromFile()`](https://github.com/cqframework/clinical_quality_language/blob/master/Src/java/cql-to-elm/src/commonMain/kotlin/org/cqframework/cql/cql2elm/CqlTranslatorOptions.kt) accepting KMP [`Path`](https://kotlinlang.org/api/kotlinx-io/kotlinx-io-core/kotlinx.io.files/-path/).


## `cql-to-elm`'s new KMP project structure

One of the motivations for converting to KMP was to publish the JavaScript version of the CQL compiler. The new JS variant is now built alongside the JVM version as part of the same `cql-to-elm` KMP project.

The JVM and JS variants use the same core CQL to ELM translator logic under `commonMain`.

One of the main functionalities unique to the JVM is the automatic loading of `ModelInfoProvider`, `LibrarySourceProvider`, and `UcumService` implementations which relies on `ServiceLoader` and (for `ModelInfoProvider` and `LibrarySourceProvider`) works the same way as before. The JS variant requires you to register the providers explicitly when configuring the `ModelManager` and `LibraryManager`.


## Kotlin code generation and multiplatform serialization

The previous version of the project used JAXB's xjc tool to generate Java classes from the ELM and ModelInfo XML schemas. The generated code was specific to the JVM and included JAXB annotations and methods for serialization and deserialization. At run time, the translator used either JAXB or Jackson to read and write ELM and ModelInfo XML and JSON, depending on which of `elm-(jackson|jaxb)` and `model-(jackson|jaxb)` were included. The translator relied on `ServiceLoader` to load the implementations of the `ElmLibraryReaderProvider`, `ElmLibraryWriterProvider`, and `ModelInfoReaderProvider` interfaces to use for this purpose.

In v4, a custom [plugin](https://github.com/cqframework/clinical_quality_language/blob/master/Src/java/buildSrc/src/main/kotlin/XsdKotlinGenTask.kt) is used to generate pure-Kotlin ELM and model info classes from the same XML schemas. These classes do not depend on JAXB but otherwise have the same structure as the previous Java classes. Runtime parsing and writing of XML is now handled by the custom multiplatform XML methods, while [`kotlinx.serialization`](https://github.com/Kotlin/kotlinx.serialization) is used for JSON. This functionality is available in the translator by default, so no peer dependencies are required.

In case you need to customize the serialization behavior for ELM, you can use the new overloads of the `CqlTranslator` methods and `LibraryManager` constructor to provide your own `ElmLibraryReaderProvider` and `ElmLibraryWriterProvider` implementations.

### XML and JSON serialization API

The [#1653](https://github.com/cqframework/clinical_quality_language/pull/1653) pull request exposes the default multiplatform serialization methods of individual ELM elements (internal in the initial v4 release). This allows users to serialize pieces of ELM trees and not only full ELM libraries.

To convert an ELM node to a JSON string, use:

```kt
val str = elmNode.toJsonObject(false).toString()
```

To generate an XML string, use:

```kt
// Sets the root tag name to be used in the output.
val rootTagName = QName("http://www.example.org/myns", "myelm")

// Pre-defines the preferred prefixes for namespace URIs in the output
// when those URIs are encountered during serialization.
val defaultNamespaces = mapOf(
  "" to "http://www.example.org/", // will become the default namespace
  "myns" to "http://www.example.org/myns",
  ...
)

// Maps the prefixes to namespace URIs encountered in the XML. Populated
// by the toXmlElement() method below.
val collectedNamespaces = mutableMapOf<String, String>()

val xmlElement = elmNode.toXmlElement(
  rootTagName,
  false,
  collectedNamespaces,
  defaultNamespaces,
)

val xmlString = toXmlString(xmlElement, collectedNamespaces)
```

## The new CQL compiler for JavaScript environments

The JS variant of the compiler is currently in beta, and its API may change in a backwards-incompatible way before reaching a stable release. The end goal is, however, to have the common API across the JVM and JS variants with minimal differences.

Here is a simple example of using the JS compiler to translate a CQL library to ELM:

```js
import { ModelManager, LibraryManager, CqlTranslator } from "@cqframework/cql/cql-to-elm";

const modelManager = new ModelManager();
// Register the necessary model info providers with the model manager here
const libraryManager = new LibraryManager(modelManager);
// Register the necessary library source providers with the library manager here
const cqlTranslator = CqlTranslator.fromText("library Test version '1.0.0'", libraryManager);
const elmJson = cqlTranslator.toJson();
```

A more complete example, including how to set up a web app that uses the JS compiler, can be found in the playground project.
