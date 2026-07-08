library ChlamydiaScreening_CDS_UsingCommon version '2'

using QUICK

include ChlamydiaScreening_Common version '2' called Common

codesystem "SNOMED": 'http://snomed.info/sct'

valueset "Reason for not performing Chlamydia Screening": 'TBD'

context Patient

define "In Demographic":
    AgeInYears() >= 16 and AgeInYears() < 24 and "Patient"."gender" in Common."Female Administrative Sex"

define "Sexually Active":
    exists (Common."Conditions Indicating Sexual Activity")
    or exists (Common."Laboratory Tests Indicating Sexual Activity")

define "No Screening":
    not exists (Common."Results Present For Chlamydia Screening" S where S."issued" during Interval[Today() - 1 years, Today()])
    and not exists (["ProcedureRequest": Common."Chlamydia Screening"] P where P."orderedOn" same day or after Today())
    and not exists (["Observation": "Reason for not performing Chlamydia Screening"])

define "Need Screening": "In Demographic" and "Sexually Active" and "No Screening"

//The following used to read "ProcedureRequest" where it now reads "Tuple"

define "ChlamydiaScreeningRequest": Tuple {
  type: Code '442487003' from "SNOMED" display 'Screening for Chlamydia trachomatis (procedure)',
  status: 'proposed'
  // values for other elements of the request...
}
