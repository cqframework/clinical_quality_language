// N.B.  This makes a ton of difference so be sure to define it correctly
//library EvaluatedResourcesTest version '1.0'
library TestSomethingSomething1241 version '1.0'

using FHIR version '4.0.1'

context Patient

define "Mother": singleton from ([RelatedPerson])

define "Child Observation":
    ["Observation"]

define "Mother Observation":
    ["Mother" -> "Observation"]