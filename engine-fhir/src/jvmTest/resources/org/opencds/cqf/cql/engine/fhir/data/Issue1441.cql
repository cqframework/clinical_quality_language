library "Issue1441" version '1'

// https://github.com/cqframework/clinical_quality_language/issues/1441
// Unions of unaliased retrieves not working

using FHIR version '4.0.1'

context Patient

define x:
  [Observation] a
    union [Procedure] b

define y:
  [Observation]
    union [Procedure]