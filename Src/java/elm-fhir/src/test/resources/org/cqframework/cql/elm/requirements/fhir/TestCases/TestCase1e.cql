library TestCase1e

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

parameter Manufacturer String

context Patient

/*
1e: Code-valued terminology filter in a retrieve referencing a custom search parameter
Bound Manufacturer: 'Canon Medical Systems'
DataRequirement
type: ImagingStudy
codeFilter: { path: manufacturer, code: "Canon Medical Systems" }
*/
define StudiesWithManufacturer:
  [ImagingStudy: manufacturer ~ Manufacturer]

