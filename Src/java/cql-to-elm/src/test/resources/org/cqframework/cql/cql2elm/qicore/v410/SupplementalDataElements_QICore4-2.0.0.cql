library SupplementalDataElements_QICore4 version '2.0.0'

/*
@update: @@BTR 2020-03-31 ->
Incremented version to 2.0.0
Updated FHIR version to 4.0.1
@@
*/

using QICore version '4.1.0'

include FHIRHelpers version '4.0.1'

valueset "ONC Administrative Sex": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113762.1.4.1'
valueset "Race": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.114222.4.11.836'
valueset "Ethnicity": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.114222.4.11.837'
valueset "Payer": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.114222.4.11.3591'

context Patient

define "SDE Ethnicity":
  Patient.ethnicity.detailed

define "SDE Payer":
    [Coverage: type in "Payer"] Payer
      return {
        code: Payer.type,
        period: Payer.period
      }

define "SDE Race":
  Patient.race.detailed

define "SDE Sex":
  case
    when Patient.gender = 'male' then Code { code: 'M', system: 'http://hl7.org/fhir/v3/AdministrativeGender', display: 'Male' }
    when Patient.gender = 'female' then Code { code: 'F', system: 'http://hl7.org/fhir/v3/AdministrativeGender', display: 'Female' }
    else null
  end
