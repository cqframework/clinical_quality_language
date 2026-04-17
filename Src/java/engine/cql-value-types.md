# CQL Value Types

The engine supports a fixed set of CQL value types that are represented using the following Kotlin classes:

- `null` value: represented using Kotlin's `null` and has type `System.Any`
- Simple types
  - Boolean: `kotlin.Boolean`
  - Integer: `kotlin.Int`
  - Long: `kotlin.Long`
  - Decimal: `org.cqframework.cql.shared.BigDecimal`
  - String: `kotlin.String`
  - Temporal types
    - DateTime: `org.opencds.cqf.cql.engine.runtime.DateTime`
    - Date: `org.opencds.cqf.cql.engine.runtime.Date`
    - Time: `org.opencds.cqf.cql.engine.runtime.Time`
- Structured types
  - Tuple: `org.opencds.cqf.cql.engine.runtime.Tuple`
  - Class types (a.k.a. named structured types)
    - System class types (clinical values)
      - Quantity: `org.opencds.cqf.cql.engine.runtime.Quantity`
      - Ratio: `org.opencds.cqf.cql.engine.runtime.Ratio`
      - Code: `org.opencds.cqf.cql.engine.runtime.Code`
      - Concept: `org.opencds.cqf.cql.engine.runtime.Concept`
      - Vocabulary types
        - CodeSystem: `org.opencds.cqf.cql.engine.runtime.CodeSystem`
        - ValueSet: `org.opencds.cqf.cql.engine.runtime.ValueSet`
    - Non-system classes (e.g. instances of `FHIR.Patient`): `org.opencds.cqf.cql.engine.runtime.ClassInstance`
- List: represented using Kotlin's `Iterable<T>`
- Interval: `org.opencds.cqf.cql.engine.runtime.Interval`

## Class types (named structured types)

Class types are structured types with an associated type name. They are represented using `ClassInstance` (except for the system class types) and not e.g. HAPI FHIR structures or other custom classes.

When the engine produces a structured type instance (e.g. when evaluating an expression), it will return a `ClassInstance` with the appropriate `QName` type. Same goes for the structured types expected as inputs to the engine, e.g. as context or parameter values.

System class types have specific semantics and behavior defined by the CQL specification. They are therefore represented using their own dedicated classes like `Quantity` and `Code`.
