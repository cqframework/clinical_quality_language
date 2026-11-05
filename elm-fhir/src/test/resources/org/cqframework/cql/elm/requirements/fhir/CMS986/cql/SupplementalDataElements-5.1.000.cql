/*
@update: GCW 2025-03-27 ->
Updated to reflect valueset name change from CMS Sex to Federal Administrative Sex
*/
library SupplementalDataElements version '5.1.000'

using QICore version '6.0.0'

include FHIRHelpers version '4.4.000' called FHIRHelpers

codesystem "SNOMEDCT": 'http://snomed.info/sct'

valueset "Ethnicity": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.114222.4.11.837'
valueset "Federal Administrative Sex": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113762.1.4.1021.121'
valueset "Payer Type": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.114222.4.11.3591'
valueset "Race": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.114222.4.11.836'

code "Male (finding)": '248153007' from "SNOMEDCT" display 'Male (finding)'
code "Female (finding)": '248152002' from "SNOMEDCT" display 'Female (finding)'

context Patient

define "SDE Ethnicity":
  Patient.ethnicity E
    return Tuple {
      codes: { E.ombCategory } union E.detailed,
      display: E.text
    }

define "SDE Payer":
  [Coverage: type in "Payer Type"] Payer
    return {
      code: Payer.type,
      period: Payer.period
    }

define "SDE Race":
  Patient.race R
    return Tuple {
      codes: R.ombCategory union R.detailed,
      display: R.text
    }

define "SDE Sex":
  case
    when Patient.sex = '248153007' then "Male (finding)"
    when Patient.sex = '248152002' then "Female (finding)"
    else null
  end