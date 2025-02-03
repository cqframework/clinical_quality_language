/*
@issue: [1204](https://github.com/cqframework/clinical_quality_language/issues/1204)
*/
library TestQuotedForwards

using FHIR version '4.0.1'

define function badFuncOne(value ThisTypeDoesNotExist) : null
