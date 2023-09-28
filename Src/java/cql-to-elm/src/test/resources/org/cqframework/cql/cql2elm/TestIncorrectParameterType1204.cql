/*
@issue: [1204](https://github.com/cqframework/clinical_quality_language/issues/1204)
*/
library TestQuotedForwards

using FHIR version '4.0.1'

// FHIR.Code is wrong:  it should be FHIR.code
define function badFuncOne(value FHIR.Code) : null
