# CQL Value Types

The engine supports a fixed set of CQL value types that are represented using the following Kotlin classes from the `org.opencds.cqf.cql.engine.runtime` package:

- Simple types
  - Boolean: `Boolean`
  - Integer: `Integer`
  - Long: `Long`
  - Decimal: `Decimal`
  - String: `String`
  - Temporal types
    - DateTime: `DateTime`
    - Date: `Date`
    - Time: `Time`
- Structured types
  - Tuple: `Tuple`
  - Class types (a.k.a. named structured types)
    - System class types (clinical values)
      - Quantity: `Quantity`
      - Ratio: `Ratio`
      - Code: `Code`
      - Concept: `Concept`
      - Vocabulary types
        - CodeSystem: `CodeSystem`
        - ValueSet: `ValueSet`
    - Non-system classes (e.g. instances of `FHIR.Patient`): `ClassInstance`
- List: `List`
- Interval: `Interval`

CQL's `null` value is represented using Kotlin's `null`.

## Class types (named structured types)

A class type is a structured type with an associated type name. Instances of class types are represented using `ClassInstance` (except for the system class types) and not HAPI FHIR structures or other custom classes.

System class types have specific semantics and behavior defined by the CQL specification. They are therefore represented using their own dedicated classes like `Quantity` and `Code`.
