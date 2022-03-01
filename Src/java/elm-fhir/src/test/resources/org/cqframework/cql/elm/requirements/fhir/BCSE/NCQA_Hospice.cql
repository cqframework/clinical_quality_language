library NCQA_Hospice version '1.0.0' 

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include NCQA_FHIRBase version '1.0.0' called NCQAFHIRBase
include NCQA_Status version '1.0.0' called Status

valueset "Hospice Encounter":'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1761'
valueset "Hospice Intervention": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1762'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Hospice Intervention or Encounter":
exists ((Status."Completed or Ongoing Procedure"([Procedure:"Hospice Intervention"])) HospiceInt
        where NCQAFHIRBase."Normalize Interval"(HospiceInt.performed) overlaps "Measurement Period"
)
or exists ((Status."Finished Encounter"(["Encounter": "Hospice Encounter"])) HospiceEnc
         where NCQAFHIRBase."Normalize Interval" (HospiceEnc.period) overlaps "Measurement Period"
)
