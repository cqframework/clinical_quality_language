library TestCase1d

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

parameter ModalityCode String

context Patient

/*
1d: Code-valued terminology filter in a retrieve referencing a parameter
Bound ModalityCode: 'CT'
DataRequirement
type: ImagingStudy
codeFilter: { path: modality, code: "CT" }
*/
define StudiesWithModality:
  [ImagingStudy: modality ~ ModalityCode]

