/*
This example is purely for illustration purposes to show how all the elements of a
FHIR-based quality measure are represented. It is intentionally simplified to show
specific aspects of a measure, and is not intended as an example of a clinically
meaningful measure.
*/
library EXMLogic version '2.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers

codesystem "Diagnosis Role": 'http://terminology.hl7.org/CodeSystem/diagnosis-role'

valueset "Emergency Department Visit" : 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.292'
valueset "Psychiatric/Mental Health Patient" : 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.299'
valueset "Hospital Settings" : 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113762.1.4.1111.126'
valueset "Chronic Liver Disease Legacy Example": 'http://hl7.org/fhir/us/cqfmeasures/ValueSet/chronic-liver-disease-legacy-example'

valueset "ONC Administrative Sex": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113762.1.4.1'
valueset "Race": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.114222.4.11.836'
valueset "Ethnicity": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.114222.4.11.837'
valueset "Payer": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.114222.4.11.3591'

code "Billing": 'billing' from "Diagnosis Role" display 'Billing'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Initial Population" :
  "Inpatient Encounter" Encounter

define "Measure Population" :
  "Initial Population"

define function "Measure Observation" (Encounter "Encounter" ) :
  duration in minutes of "Related ED Visit"(Encounter).period

define "Stratifier 1" :
  "Inpatient Encounter" Encounter
    where not (PrincipalDiagnosis(Encounter).code in "Psychiatric/Mental Health Patient")

define "Stratifier 2" :
  "Inpatient Encounter" Encounter
    where PrincipalDiagnosis(Encounter).code in "Psychiatric/Mental Health Patient"

define "Stratifier 3" :
  "Inpatient Encounter" Encounter
    where PrincipalDiagnosis(Encounter) is null

define "Stratifier 4" :
  "Inpatient Encounter" Encounter
    where PrincipalDiagnosis(Encounter) is null

define "Inpatient Encounter" :
  ["Encounter"] Encounter
    where LengthInDays(Encounter.period) <= 120
      and Encounter.period ends during "Measurement Period"

define function "PrincipalDiagnosis"(Encounter Encounter):
	(singleton from (Encounter.diagnosis D where D.use ~ ToConcept("Billing") and D.rank.value = 1)) PD
		return singleton from ([Condition: id in "GetId"(PD.condition.reference)])

define function "LengthInDays"(Value Interval<DateTime>):
	difference in days between start of Value and end of Value

define function "GetId"(uri String):
	Last(Split(uri, '/'))

//Measure Observation
define function "Related ED Visit" (Encounter "Encounter" ) :
  Last(["Encounter" : "Emergency Department Visit"] ED
    where ED.status = 'finished'
      and ED.period ends 1 hour or less before start of Encounter.period
    sort by start of period )

define "SDE Ethnicity":
  (flatten (
    Patient.extension Extension
      where Extension.url = 'http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity'
        return Extension.extension
  )) E
    where E.url = 'ombCategory'
      or E.url = 'detailed'
    return E.value as Coding

define "SDE Payer":
    [Coverage: type in "Payer"] Payer
      return {
        code: Payer.type,
        period: Payer.period
      }

define "SDE Race":
  (flatten (
    Patient.extension Extension
      where Extension.url = 'http://hl7.org/fhir/us/core/StructureDefinition/us-core-race'
        return Extension.extension
  )) E
    where E.url = 'ombCategory'
      or E.url = 'detailed'
    return E.value as Coding

define "SDE Sex":
  case
    when Patient.gender = 'male' then Code { code: 'M', system: 'http://hl7.org/fhir/v3/AdministrativeGender', display: 'Male' }
    when Patient.gender = 'female' then Code { code: 'F', system: 'http://hl7.org/fhir/v3/AdministrativeGender', display: 'Female' }
    else null
  end
