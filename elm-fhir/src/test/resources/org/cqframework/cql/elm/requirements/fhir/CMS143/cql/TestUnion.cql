library TestUnion version '0.0.003'

using QICore version '4.1.1'

include FHIRHelpers version '4.1.000' called FHIRHelpers
include CQMCommon version '1.0.000' called CQMCommon
include FHIRCommon version '4.1.000' called FHIRCommon
include QICoreCommon version '1.2.000' called QICoreCommon

codesystem "ActCode": 'http://terminology.hl7.org/CodeSystem/v3-ActCode'

valueset "Office Visit": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "Ophthalmological Services": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1285'
valueset "Outpatient Consultation": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1008'

code "virtual": 'VR' from "ActCode" display 'virtual'
code "AMB": 'AMB' from "ActCode" display 'Ambulatory'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Qualifying Encounter":
  ["Encounter": "Office Visit"]
    union ["Encounter": "Ophthalmological Services"]
    union ["Encounter": "Outpatient Consultation"]

define "Qualifying Encounter During Measurement Period":
  "Qualifying Encounter" QualifyingEncounter
    where QualifyingEncounter.period during "Measurement Period"
      and QualifyingEncounter.class !~ "virtual"
      and QualifyingEncounter.class ~ "AMB"

define "Qualifying Encounter During Measurement Period Expanded":
  (["Encounter": "Office Visit"]
    union ["Encounter": "Ophthalmological Services"]
    union ["Encounter": "Outpatient Consultation"]
  ) QualifyingEncounter
    where QualifyingEncounter.period during "Measurement Period"
      and QualifyingEncounter.class !~ "virtual"
      and QualifyingEncounter.class ~ "AMB"
