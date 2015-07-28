CQL Execution Framework Reference Implementation
================================================

The reference implementation for executing CQL is currently under development. The reference implementation is intended to be used in CQF pilots and eventually integrated into production eCQM tools such as Bonnie and Cypress.

The CQL execution framework is licensed under the open source [Apache Version 2.0](../../../LICENSE) license, and available as part of the [clinical_quality_language](https://github.com/cqframework/clinical_quality_language) project on GitHub.

Technologies
------------

The CQL execution framework is written in [CoffeeScript](http://coffeescript.org/). CoffeeScript is an elegant, yet powerful, scripting language that compiles down to JavaScript. The CoffeeScript source code allows the reference implementation to be easily read and understood by developers of most any language (due to its simplicity). The JavaScript execution code allows the reference implementation to be integrated into a variety of environments, including servers, other languages’ runtime environments, and standard web browsers.

The CQL execution framework tests and examples are configured to run using [Node.js](http://nodejs.org/), but can be easily integrated into other JavaScript runtime environments. Initial efforts to run the CQL execution framework in Java’s embedded [Rhino](https://developer.mozilla.org/en-US/docs/Mozilla/Projects/Rhino) engine have been successful and will be integrated into the clinical_quality_language project in the near future. This will allow the reference implementation to run in pure Java environments.

The CQL execution framework does not currently rely on any backend database for storing patient records. All records are stored as flat files or passed to the execution framework as in-memory instances of `Patient` classes. While this is not the most efficient approach, it is the simplest approach to understand and implement across a variety of use cases.

Approach
--------

### JSON ELM

Despite its name, the CQL execution framework does not execute CQL directly. Instead, it executes a JSON representation of the ELM. The [cql-to-elm](../../java/cql-to-elm) project is a reference implementation for translating CQL to ELM in XML or JSON.

Consider the following CQL:

```
define AdditionExample = 1 + 2
```

The expression 1 + 2 is represented in JSON ELM as follows:

```json
{
  "type" : "Add",
  "operand" : [ {
    "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
    "value" : "1",
     "type" : "Literal"
  }, {
    "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
    "value" : "2",
    "type" : "Literal"
  } ]
}
```

### ELM Expression Classes

Each ELM expression has a corresponding class defined in the CoffeeScript CQL execution framework. These classes all extend a common `Expression` class and define, at a minimum, these components:

1.	A `constructor` that takes a JSON ELM object as its argument
2.	An `exec` function that takes a `Context` object as its argument

The `constructor` is responsible for setting class properties from the JSON ELM object and for converting property values from nested JSON to their corresponding `Expression` sub-classes. The `exec` function is responsible for executing the intended logic and returning the result.

The following is an example of the `Add` class that corresponds to the JSON ELM example in the previous section:

```coffee
class Add extends Expression
  constructor: (json) ->
    super
    @arg1 = new IntegerLiteral(json.operand[0])
    @arg2 = new IntegerLiteral(json.operand[1])

  exec: (ctx) ->
    @arg1.exec(ctx) + @arg2.exec(ctx)
```

When the `constructor` is passed the JSON ELM from the previous example, it constructs `IntegerLiteral` classes from the `operand` elements, and sets the `@arg1` and `@arg2` properties to the resulting `IntegerLiteral` instances.

When `exec` is called, it calls `exec` on `@arg1` and `@arg2` (resulting in the primitives `1` and `2`) and then adds them using the native `+` operator. In CoffeeScript, the last line of a function is an implicit return, so it returns the result of the native addition operation.

Note that the actual reference implementation of `Add` differs from this example in that it can handle other types of operands (since not all addition is on `IntegerLiteral` expressions). It also utilizes common functions from its superclass, resulting in an actual implementation that is more flexible and less verbose than the implementation in this example.

This is the core of how all operations are defined and executed in the CQL execution framework. Since ELM is an AST, execution is simply a chained execution down the tree.

### PatientSource

The CQL execution framework is implemented with FHIR Profiles for Clinical Quality (QUICK) as the primary data model. Access to the data model, however, always occurs through a `PatientSource` class, allowing the actual backend model implementation to be replaced with another implementation.

When the CQL execution framework executes a CQL library, it iterates over the patients provided by the `PatientSource`, calculating each expression in the library’s `Patient` context for each patient. In the `Patient` context, retrieves are always executed against the current patient record. In order to support patient-specific retrieves and record access, the `Patient` class must implement a small number of predefined functions (such as `find` and `get`).

Ideally, a `PatientSource` should provide the pre-filtered set of patients, based on the initial data requiremements (gleaned from the library’s retrieve statements). In the current reference implementation, the `PatientSource` is populated with an array of JSON-formatted patients, usually from a flat file.

### CodeService

In order for the CQL execution framework to determine if a code is in a valueset, it must be able to resolve the valueset to a list of codes. Valueset resolution always occurs through a `CodeService` class, allowing the actual backend implementation to be replaced with another implementation. In the current reference implementation, the CodeService is loaded with a static JSON map of valuesets and codes, usually from a flat file. In an ideal implementation, the CodeService should access a local database of valuesets or an API to a valueset service.

### Executor

The CQL execution framework provides a basic Executor class for executing a cql document over a PatientSource. An instance of the Executor class provides a wrapping element around a Library instance, a CodeService instance (if required) and a set of CQL input parameters. Once configured, an Executor class can be used multiple times to execute over an arbitary number of PatientSource instances.

Executing CQL Libraries
-----------------------

The following is an example of a CoffeeScript file for executing a CQL library:

```coffee
cql = require './cql/cql'
patients = require './data/example-patients'
valuesets = require './data/example-valuesets'
measure = require './example-measure'

lib = new cql.Library(measure)
patientSource = new cql.PatientSource(patients)
codeService = new cql.CodeService(valuesets)

parameters = {
  "MeasurementPeriod" : new cql.Interval(
    new cql.DateTime(2013, 1, 1, 0, 0, 0, 0),
    new cql.DateTime(2014, 1, 1, 0, 0, 0, 0), true, false)
}

executor = new cql.Executor(lib, codeService, parameters)
result = executor.exec(patientSource)
```

The first line imports the CQL execution framework library, while the next three lines import the measure JSON ELM, patient data, and valueset data. The next three lines then construct the CQL `Library`, `PatientSource`, and `CodeService` using the imported data. The `parameters` definition overrides the `"MeasurementPeriod"` parameter with an interval representing the entire year of 2013. Finally, the last two lines construct an Executor object that will execute the cql document against the supplied PatientSource.

The result of the execution is a CQL `Results` object containing a list of patients and their calculated values for each named expression in the `Patient` context. If the library contained a `Population` context, the calculated value of the named expressions for the `Population` will be included in the `Results` as well.

Besides the `exec(patient_source)` method, the Executor class contains a couple of additional convenience methods for executing cql documents against Patient Sources. The first additional method, `exec_patient_context(patient_source)`, will execute only the expressions defined in the CQL library's Patient Context. Any statements declared in the Population context will be ignored. The other method, `exec_expression(patient_source)` executes a single expression in the CQL document's Patient Context, along with any expressions that are called internally by the expression to be executed.

Current Status
--------------

The CQL execution framework is still evolving and is not yet complete. The patient API, code service API, and format of results are likely to change quite a bit as implementation matures. In addition, there are still CQL/ELM operators not yet implemented in the execution framework.
