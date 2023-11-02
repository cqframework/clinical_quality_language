library TestIdentifierCaseMismatch

using FHIR version '4.0.1'

// FHIR.Code is wrong:  it should be FHIR.code
define function badFuncOne(value FHIR.Code) : null
