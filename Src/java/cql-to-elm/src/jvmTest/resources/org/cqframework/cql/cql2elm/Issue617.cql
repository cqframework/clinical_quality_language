/*
@issue: [#617](https://github.com/cqframework/clinical_quality_language/issues/617)
*/

library Issue617

using FHIR version '4.0.1'

context Patient

define "Boolean Implies": false implies true
