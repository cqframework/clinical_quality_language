/*
@update: JSR 2024-04-04 ->
Updated FHIRHelpers version to 4.4.000.
Added Male and Female codes and related AdministrativeGender code system
Updated SDE Sex definition to use added codes from FHIR administrative-gender code system.
*/
library SupplementalDataElements version '3.5.000'

using QICore version '4.1.1'

include FHIRHelpers version '4.4.000' called FHIRHelpers

codesystem "AdministrativeGender": 'http://hl7.org/fhir/administrative-gender'

valueset "Ethnicity": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.114222.4.11.837'
valueset "ONC Administrative Sex": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113762.1.4.1'
valueset "Payer Type": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.114222.4.11.3591'
valueset "Race": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.114222.4.11.836'

code "M": 'M' from "AdministrativeGender" display 'Male'
code "F": 'F' from "AdministrativeGender" display 'Female'

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
    when Patient.gender = 'male' then "M"
    when Patient.gender = 'female' then "F"
    else null
  end