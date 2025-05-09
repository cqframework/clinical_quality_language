/*
@update: BTR 2020-03-31 ->
Incremented version to 2.0.0

@update: BTR 2022-05-26 ->
Updated FHIR version to 4.0.1
Updated FHIRHelpers version to 4.0.002
Updated for AU 2022 Content

@update: JSR 2023-07-18
Updated "Payer" value set name to "Payer Type"

@update: JSR 2023-07-20 ->
Updated FHIRHelpers version to 4.3.000
*/
library SupplementalDataElements version '3.4.000'

using QICore version '4.1.1'

include FHIRHelpers version '4.3.000' called FHIRHelpers

valueset "Ethnicity": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.114222.4.11.837'
valueset "ONC Administrative Sex": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113762.1.4.1'
valueset "Payer Type": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.114222.4.11.3591'
valueset "Race": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.114222.4.11.836'

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
      when Patient.gender = 'male' then Code { code: 'M', system: 'http://hl7.org/fhir/v3/AdministrativeGender', display: 'Male' }
      when Patient.gender = 'female' then Code { code: 'F', system: 'http://hl7.org/fhir/v3/AdministrativeGender', display: 'Female' }
      else null
    end