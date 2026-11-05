library TSCComponent version '0.0.001'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.001' called FHIRHelpers
include SupplementalDataElementsFHIR4 version '2.0.000' called SDE
include MATGlobalCommonFunctionsFHIR4 version '5.0.000' called Global

codesystem "LOINC": 'http://loinc.org'
codesystem "CPT": 'http://www.ama-assn.org/go/cpt'

valueset "Annual Wellness Visit": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1240'
valueset "Face-to-Face Interaction": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1048'
valueset "Home Healthcare Services": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Limited Life Expectancy": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1259'
valueset "Medical Reason": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1007'
valueset "Occupational Therapy Evaluation": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1011'
valueset "Office Visit": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "Ophthalmological Services": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1285'
valueset "Physical Therapy Evaluation": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1022'
valueset "Preventive Care Services - Established Office Visit, 18 and Up": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1025'
valueset "Preventive Care Services - Group Counseling": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1027'
valueset "Preventive Care Services - Other": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1030'
valueset "Preventive Care Services-Individual Counseling": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1026'
valueset "Preventive Care Services-Initial Office Visit, 18 and Up": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1023'
valueset "Psych Visit - Diagnostic Evaluation": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1492'
valueset "Psych Visit - Psychotherapy": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1496'
valueset "Psychoanalysis": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1141'
valueset "Speech and Hearing Evaluation": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1530'
valueset "Tobacco Non-User": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1189'
valueset "Tobacco Use Cessation Counseling": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.509'
valueset "Tobacco Use Cessation Pharmacotherapy": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1190'
valueset "Tobacco Use Screening": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1278'
valueset "Tobacco User": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1170'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Health behavior assessment, or re-assessment (ie, health-focused clinical interview, behavioral observations, clinical decision making)": '96156' from "CPT" display 'Health behavior assessment, or re-assessment (ie, health-focused clinical interview, behavioral observations, clinical decision making)'
code "Health behavior intervention, individual, face-to-face; initial 30 minutes": '96158' from "CPT" display 'Health behavior intervention, individual, face-to-face; initial 30 minutes'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "SDE Ethnicity":
  SDE."SDE Ethnicity"

define "SDE Payer":
  SDE."SDE Payer"

define "SDE Race":
  SDE."SDE Race"

define "SDE Sex":
  SDE."SDE Sex"

define "Preventive Visit During Measurement Period":
  ( ["Encounter": "Annual Wellness Visit"]
      union ["Encounter": "Preventive Care Services - Established Office Visit, 18 and Up"]
      union ["Encounter": "Preventive Care Services - Group Counseling"]
      union ["Encounter": "Preventive Care Services - Other"]
      union ["Encounter": "Preventive Care Services-Individual Counseling"]
      union ["Encounter": "Preventive Care Services-Initial Office Visit, 18 and Up"] ) PreventiveEncounter
      where PreventiveEncounter.period during "Measurement Period"
        and PreventiveEncounter.status = 'finished'

define "Denominator 1":
  "Initial Population"

define "Denominator 3":
  "Initial Population"

define "Initial Population":
  exists ( ["Patient"] BirthDate
        where Global."CalendarAgeInYearsAt" ( FHIRHelpers.ToDate ( BirthDate.birthDate ), start of "Measurement Period" ) >= 18
    )
      and ( Count("Office Based Visit During Measurement Period")>= 2
          or exists "Preventive Visit During Measurement Period"
      )

define "Medical Reason for Not Screening for Tobacco Use":
  ["Observation": "Tobacco Use Screening"] NoTobaccoUseScreening
      where NoTobaccoUseScreening.issued during "Measurement Period"
        and "Not Done"(NoTobaccoUseScreening)
        and "Not Done Reason in Medical Reason"(NoTobaccoUseScreening)
        and NoTobaccoUseScreening.status = 'final'

define "Medical Reason for Not Ordering Tobacco Cessation Pharmacotherapy":
  ["MedicationRequest": "Tobacco Use Cessation Pharmacotherapy"] NoCessationPharmacotherapyOrdered
      where NoCessationPharmacotherapyOrdered.authoredOn during "Measurement Period"
        and NoCessationPharmacotherapyOrdered.status = 'completed'
        and ( ( "Not Done"(NoCessationPharmacotherapyOrdered)
              and "Not Done Reason in Medical Reason"(NoCessationPharmacotherapyOrdered)
          )
            or ( NoCessationPharmacotherapyOrdered.doNotPerform = true
                and NoCessationPharmacotherapyOrdered.statusReason in "Medical Reason"
            )
        )

define "Medical Reason for Not Counseling Tobacco User":
  ["Procedure": "Tobacco Use Cessation Counseling"] NoTobaccoCessationCounseling
      where Global."Normalize Interval" ( NoTobaccoCessationCounseling.performed ) starts during "Measurement Period"
        and ( ( NoTobaccoCessationCounseling.status = 'not-done'
              and NoTobaccoCessationCounseling.statusReason in "Medical Reason"
          )
            or ( "Not Done"(NoTobaccoCessationCounseling)
                and "Not Done Reason in Medical Reason"(NoTobaccoCessationCounseling)
            )
        )

define "Tobacco Cessation Pharmacotherapy Ordered":
  ["MedicationRequest": "Tobacco Use Cessation Pharmacotherapy"] CessationPharmacotherapyOrdered
      where CessationPharmacotherapyOrdered.authoredOn during "Measurement Period"
        and CessationPharmacotherapyOrdered.status = 'active'

// MedicationStatement is not available in R5, known publication issue
//define "Active Pharmacotherapy for Tobacco Cessation":
//  ["MedicationStatement": "Tobacco Use Cessation Pharmacotherapy"] TakingCessationPharmacotherapy
//      where Global."Normalize Interval" ( TakingCessationPharmacotherapy.effective ) overlaps after "Measurement Period"
//        and TakingCessationPharmacotherapy.status in { 'active', 'completed' }

define "Tobacco Cessation Counseling Given":
  ["Encounter": "Tobacco Use Cessation Counseling"] TobaccoCessationCounseling
      where TobaccoCessationCounseling.period during "Measurement Period"
        and TobaccoCessationCounseling.status = 'finished'

define "Numerator 2":
  exists "Tobacco Cessation Counseling Given"
      or exists "Tobacco Cessation Pharmacotherapy Ordered"
      //or exists "Active Pharmacotherapy for Tobacco Cessation"

define "Has Limited Life Expectancy":
  exists ( ["Condition"] LimitedLifeExpectancy
        where Global."Prevalence Period" ( LimitedLifeExpectancy ) overlaps after "Measurement Period"
          and LimitedLifeExpectancy.clinicalStatus ~ ToConcept(Global."active")
    )

define "Denominator Exceptions 1":
  exists "Medical Reason for Not Screening for Tobacco Use"
      or "Has Limited Life Expectancy"

define "Denominator Exceptions 2":
  exists "Medical Reason for Not Counseling Tobacco User"
      or exists "Medical Reason for Not Ordering Tobacco Cessation Pharmacotherapy"
      or "Has Limited Life Expectancy"

define "Denominator Exceptions 3":
  exists "Medical Reason for Not Screening for Tobacco Use"
      or exists "Medical Reason for Not Counseling Tobacco User"
      or exists "Medical Reason for Not Ordering Tobacco Cessation Pharmacotherapy"
      or "Has Limited Life Expectancy"

define "Office Based Visit During Measurement Period":
  ( ["Encounter": "Health behavior intervention, individual, face-to-face; initial 30 minutes"]
      union ["Encounter": "Health behavior assessment, or re-assessment (ie, health-focused clinical interview, behavioral observations, clinical decision making)"]
      union ["Encounter": "Home Healthcare Services"]
      union ["Encounter": "Occupational Therapy Evaluation"]
      union ["Encounter": "Office Visit"]
      union ["Encounter": "Ophthalmological Services"]
      union ["Encounter": "Physical Therapy Evaluation"]
      union ["Encounter": "Psych Visit - Diagnostic Evaluation"]
      union ["Encounter": "Psych Visit - Psychotherapy"]
      union ["Encounter": "Psychoanalysis"]
      union ["Encounter": "Speech and Hearing Evaluation"] ) OfficeBasedEncounter
      where OfficeBasedEncounter.period during "Measurement Period"
        and OfficeBasedEncounter.status = 'finished'

define "Most Recent Tobacco Use Screening Indicates Tobacco Non User":
  ( Last(["Observation": "Tobacco Use Screening"] TobaccoUseScreening
        where Global."Normalize Interval"(TobaccoUseScreening.effective)overlaps "Measurement Period"
        sort by start of Global."Normalize Interval"(effective)
    )) MostRecentTobaccoUseScreening
      where MostRecentTobaccoUseScreening.value in "Tobacco Non-User"

define "Most Recent Tobacco Use Screening Indicates Tobacco User":
  ( Last(["Observation": "Tobacco Use Screening"] TobaccoUseScreening
        where Global."Normalize Interval"(TobaccoUseScreening.effective)overlaps "Measurement Period"
        sort by start of Global."Normalize Interval"(effective)
    )) MostRecentTobaccoUseScreening
      where MostRecentTobaccoUseScreening.value in "Tobacco User"

define "Denominator 2":
  "Initial Population"
      and "Most Recent Tobacco Use Screening Indicates Tobacco User" is not null

define "Numerator 1":
  "Most Recent Tobacco Use Screening Indicates Tobacco Non User" is not null
      or "Most Recent Tobacco Use Screening Indicates Tobacco User" is not null

define "Numerator 3":
  "Most Recent Tobacco Use Screening Indicates Tobacco Non User" is not null
      or ( "Most Recent Tobacco Use Screening Indicates Tobacco User" is not null
          and ( exists "Tobacco Cessation Counseling Given"
              or exists "Tobacco Cessation Pharmacotherapy Ordered"
              //or exists "Active Pharmacotherapy for Tobacco Cessation"
          )
      )

define function "Not Done"(res DomainResource ):
  if ( res R
      where ( "GetModiferExtension"(R, 'http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-notDone').value = true )
  ) is not null then true
    else false

define function "Not Done Reason in Medical Reason"(res DomainResource ):
  if ( res R
      where "GetModiferExtension"(R, 'http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-notDoneReason').value in "Medical Reason"
  ) is not null then true
    else false

define function "GetModifierExtensions"(res DomainResource, url String ):
  res.modifierExtension E
    where E.url = ( url )
    return E

define function "GetModiferExtension"(res DomainResource, url String ):
  singleton from "GetModifierExtensions"(res, url)
