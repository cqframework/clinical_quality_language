/*
@issue: [#547](https://github.com/cqframework/clinical_quality_language/issues/547)
*/

library Issue547

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

context Patient

define Conditions: singleton from [Condition]
