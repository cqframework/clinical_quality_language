# engine-fhir

This module contains the implementation of the FHIR model resolvers and retrieve providers for the CQL engine, based on the HAPI FHIR library.


## Converting from HAPI FHIR structures to engine value types

The `FhirModelResolver.toCqlValue()` helper method converts HAPI FHIR structures (resources and individual elements) to appropriate `ClassInstance`s, which can then be used as inputs to the engine. Given a FHIR structure, the method:

- walks all child elements of the FHIR structure (using introspection methods and reflections on HAPI FHIR classes) and adds an entry for every child in the named tuple being built,
- checks max cardinality to determine if a CQL list or a singleton to use as the value,
- if the child FHIR element has no values, sets the element value to `null` in the tuple.

In general, the least nested structure is chosen for the value of an element, e.g.:
- when the element is List-typed but has no values, `null` is chosen instead of an empty list
- when an element is FHIR primitive-typed (e.g. `FHIR.string` or `FHIR.code`-typed) and does not have value/id/extensions, `null` is chosen instead of a blank structure like `FHIR.string { id: null, extension: null, value: null }`
