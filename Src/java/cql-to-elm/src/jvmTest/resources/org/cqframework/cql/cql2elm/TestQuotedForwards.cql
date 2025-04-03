/*
@issue: [827](https://github.com/cqframework/clinical_quality_language/issues/827)
*/
library TestQuotedForwards

using FHIR version '4.0.1'

context Patient

define "Referencing Expression":
    "This is the \"referenced\" expression"

define "This is the \"referenced\" expression":
    'The reference resolved!'

