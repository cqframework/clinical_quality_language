library OpioidCDSCommon version '2.0.1'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include OMTKLogicMK2020 version '2.0.1' called OMTKLogic
include OpioidCDSCommonConfig version '2.0.1' called Config

codesystem "LOINC": 'http://loinc.org'
codesystem "SNOMED": 'http://snomed.info/sct'
codesystem "Medication Request Category Codes": 'http://terminology.hl7.org/CodeSystem/medicationrequest-category'
codesystem "Medication Request Status Codes": 'http://hl7.org/fhir/medication-request-status'
codesystem "Condition Clinical Status Codes": 'http://terminology.hl7.org/CodeSystem/condition-clinical'
codesystem "FHIR Condition Category Codes": 'http://hl7.org/fhir/condition-category'
codesystem "US Core Condition Category Codes": 'http://hl7.org/fhir/us/core/CodeSystem/condition-category'

// Expression-based
valueset "Opioid analgesics with ambulatory misuse potential": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/opioid-analgesics-with-ambulatory-misuse-potential'
valueset "Extended release opioid with ambulatory misuse potential": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/extended-release-opioid-with-ambulatory-misuse-potential'
valueset "Buprenorphine and methadone medications": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/buprenorphine-and-methadone-medications'

// Enumerated-compose
valueset "Limited life expectancy conditions": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/limited-life-expectancy-conditions'
valueset "Therapies indicating end of life care": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/therapies-indicating-end-of-life-care'
valueset "Conditions likely terminal for opioid prescribing": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/conditions-likely-terminal-for-opioid-prescribing'
valueset "CDC malignant cancer conditions": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/cdc-malignant-cancer-conditions'
valueset "Oncology specialty designations (NUCC)": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/oncology-specialty-designations'
valueset "Opioid misuse disorders": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/opioid-misuse-disorders'
valueset "Substance misuse behavioral counseling": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/substance-misuse-behavioral-counseling'
valueset "Conditions documenting substance misuse": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/conditions-documenting-substance-misuse'
// Harvested from VSAC - OID: 2.16.840.1.113883.3.464.1003.101.12.1001
// Reviewed with Terminology, 2020-02-05 - Value set name in VSAC is "Office Visit", need to verify suitability
valueset "Office Visit": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/office-visit'
valueset "Opioid counseling procedure": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/opioid-counseling-procedure'

/* Existing sets for first six recs */
valueset "Benzodiazepine medications": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/benzodiazepine-medications'
valueset "Non-opioid drug urine screening": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/non-opioid-drug-urine-screening'
valueset "Naloxone medications": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/naloxone-medications'
valueset "Opioid misuse assessment procedure": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/opioid-misuse-assessment-procedure'
valueset "Opioid drug urine screening": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/opioid-drug-urine-screening'
valueset "Hospice Disposition": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/hospice-disposition' // Harvested from VSAC - OID: 2.16.840.1.113762.1.4.1108.15
valueset "Hospice Finding Codes": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/hospice-finding'
valueset "Hospice Procedure Codes": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/hospice-procedure'

valueset "Pain treatment plan": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/pain-treatment-plan'
valueset "Pain management procedure": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/pain-management-procedure'
valueset "PDMP review procedure": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/pdmp-review-procedure'
valueset "PDMP data reviewed finding": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/pdmp-data-reviewed-finding'

code "Community": 'community' from "Medication Request Category Codes"
code "Active Condition": 'active' from "Condition Clinical Status Codes"
code "Active MedicationRequest": 'active' from "Medication Request Status Codes"

code "Encounter Diagnosis": 'encounter-diagnosis' from "FHIR Condition Category Codes" display 'Encounter Diagnosis'
code "Problem List Item": 'problem-list-item' from "FHIR Condition Category Codes" display 'Problem List Item'
code "Health Concern": 'health-concern' from "US Core Condition Category Codes" display 'Health Concern'

// TODO: code "Nonpharmacologic therapy and nonopioid pharmocologic": 'TODO' from "TODO"

context Patient

// TODO: Capture process decisions for long-term opioid use
define IsForChronicPain: true

define "Active Ambulatory Opioid Rx":
  /* ("Get Active Ambulatory Medication Requests"([MedicationRequest: status in "Active MedicationRequest"])) Rx */
  (
    ("Get Active Ambulatory Medication Requests"([MedicationRequest])) MR
      where date from MR.authoredOn 2 years or less on or before Today()
        and MR.medication is Reference or MR.medication in "Opioid analgesics with ambulatory misuse potential"
  ) Rx
  // NOTE: changed to singleton
    let Med: if Rx.medication is Reference then singleton from ([Medication: id in (Last(Split((Rx.medication as FHIR.Reference).reference, '/')))]) else null
    where not(Rx.medication is Reference) or Med.code in "Opioid analgesics with ambulatory misuse potential"
      return
        MedicationRequest {
          id: Rx.id,
          status: Rx.status,
          intent: Rx.intent,
          category: Rx.category,
          medication: if Rx.medication is Reference then Med.code else Rx.medication as CodeableConcept,
          subject: Rx.subject,
          recorder: Rx.recorder,
          dosageInstruction: Rx.dosageInstruction,
          dispenseRequest: Rx.dispenseRequest
        }

define "Active Ambulatory Benzodiazepine Rx":
/* ("Get Active Ambulatory Medication Requests"([MedicationRequest: status in "Active MedicationRequest"])) Rx */
(
  ("Get Active Ambulatory Medication Requests"([MedicationRequest])) MR
    where date from MR.authoredOn 2 years or less on or before Today()
      and MR.medication is Reference or MR.medication in "Benzodiazepine medications"
) Rx
  let Med: if Rx.medication is Reference then singleton from ([Medication: id in (Last(Split((Rx.medication as FHIR.Reference).reference, '/')))]) else null
  where not(Rx.medication is Reference) or Med.code in "Benzodiazepine medications"
    return
      MedicationRequest {
        id: Rx.id,
        status: Rx.status,
        intent: Rx.intent,
        category: Rx.category,
        medication: if Rx.medication is Reference then Med.code else Rx.medication as CodeableConcept,
        subject: Rx.subject,
        recorder: Rx.recorder,
        dosageInstruction: Rx.dosageInstruction,
        dispenseRequest: Rx.dispenseRequest
      }

define "Active Ambulatory Naloxone Rx":
/* ("Get Active Ambulatory Medication Requests"([MedicationRequest: status in "Active MedicationRequest"])) Rx */
(
  ("Get Active Ambulatory Medication Requests"([MedicationRequest])) MR
    where date from MR.authoredOn 2 years or less on or before Today()
      and MR.medication is Reference or MR.medication in "Naloxone medications"
) Rx
  let Med: if Rx.medication is Reference then singleton from ([Medication: id in (Last(Split((Rx.medication as FHIR.Reference).reference, '/')))]) else null
  where not(Rx.medication is Reference) or Med.code in "Naloxone medications"
    return
      MedicationRequest {
        id: Rx.id,
        status: Rx.status,
        intent: Rx.intent,
        category: Rx.category,
        medication: if Rx.medication is Reference then Med.code else Rx.medication as CodeableConcept,
        subject: Rx.subject,
        recorder: Rx.recorder,
        dosageInstruction: Rx.dosageInstruction,
        dispenseRequest: Rx.dispenseRequest
      }

define function "Get Active Ambulatory Medication Requests" (value List<MedicationRequest>) returns List<MedicationRequest>:
  value Rx
    where Rx.status.value = 'active'
      and exists (
        Rx.category RxCategory
          where FHIRHelpers.ToConcept(RxCategory) ~ "Community"
      )

define function "Is Ambulatory Medication Request?" (value List<MedicationRequest>) returns List<MedicationRequest>:
  value Rx
    where exists (
        Rx.category RxCategory
          where FHIRHelpers.ToConcept(RxCategory) ~ "Community"
      )

define function "Is Opioid Analgesic with Ambulatory Misuse Potential?"(value List<MedicationRequest>):
  value Rx
    let MedRef: if (Rx.medication is FHIR.Reference) then (Rx.medication as FHIR.Reference).reference else null,
      Med: if MedRef is null then (Rx.medication as FHIR.CodeableConcept) else SingletonFrom([Medication: id in (Last(Split(MedRef, '/')))] Med return Med.code)
    where Med in "Opioid analgesics with ambulatory misuse potential"
      and exists (
        Rx.category RxCategory
          where FHIRHelpers.ToConcept(RxCategory) ~ "Community"
      )
    return
          MedicationRequest {
            id: Rx.id,
            status: Rx.status,
            intent: Rx.intent,
            category: Rx.category,
            medication: FHIR.CodeableConcept{ coding: Med.coding },
            subject: Rx.subject,
            authoredOn: Rx.authoredOn,
            recorder: Rx.recorder,
            dosageInstruction: Rx.dosageInstruction,
            dispenseRequest: Rx.dispenseRequest
          }

define function "Is Benzodiazepine?"(value List<MedicationRequest>):
  value Rx
    let MedRef: if (Rx.medication is FHIR.Reference) then (Rx.medication as FHIR.Reference).reference else null,
      Med: if MedRef is null then (Rx.medication as FHIR.CodeableConcept) else SingletonFrom([Medication: id in (Last(Split(MedRef, '/')))] Med return Med.code)
    where Med in "Benzodiazepine medications"
      and exists (
        Rx.category RxCategory
          where FHIRHelpers.ToConcept(RxCategory) ~ "Community"
      )
    return
          MedicationRequest {
            id: Rx.id,
            status: Rx.status,
            intent: Rx.intent,
            category: Rx.category,
            medication: FHIR.CodeableConcept{ coding: Med.coding },
            subject: Rx.subject,
            authoredOn: Rx.authoredOn,
            recorder: Rx.recorder,
            dosageInstruction: Rx.dosageInstruction,
            dispenseRequest: Rx.dispenseRequest
          }

define "End of Life Assessment":
    // 1. Conditions indicating end of life or with limited life expectancy
    exists (
      "Conditions Indicating End of Life or With Limited Life Expectancy"
    )
    // 2. Admitted/referred/discharged to hospice care
    or exists (
      "Admitted/Referred/Discharged to Hospice Care"
    )
    // 3. Medications indicating end of life
    /* or exists (
      "Medications Indicating End of Life"
    ) */

define "US Core-Categorized Conditions":
  [Condition: category in "Encounter Diagnosis"]
  union
  [Condition: category in "Problem List Item"]
  union
  [Condition: category in "Health Concern"]

define "Conditions Indicating End of Life or With Limited Life Expectancy":
  (
    "US Core-Categorized Conditions" C
      where C.code in "Conditions likely terminal for opioid prescribing"
        and exists (
          C.clinicalStatus.coding Coding
            where FHIRHelpers.ToCode(Coding) ~ "Active Condition"
        )
  )
  union
  (
    "US Core-Categorized Conditions" C
      where C.code in "Limited life expectancy conditions"
        and exists (
          C.clinicalStatus.coding Coding
            where FHIRHelpers.ToCode(Coding) ~ "Active Condition"
        )
  )

define "Admitted/Referred/Discharged to Hospice Care":
  (
    [Procedure: code in "Hospice Procedure Codes"] P
      where P.status.value in { 'in-progress', 'completed' }
  )
  union
  (
    [ServiceRequest: code in "Hospice Procedure Codes"] E
      where E.status.value in { 'planned', 'arrived', 'in-progress', 'finished', 'onleave' }
  )
  union
  (
    if (Config."Hospice Findings Exclusion Enabled") then
      [Observation: code in "Hospice Finding Codes"] O
        where not (O.status.value in { 'unknown', 'entered-in-error', 'cancelled' })
    else
      {}
  )
  union
  (
    [Encounter] E
      where date from E.period.start 1 year or less on or before Today()
        and (
          if E.hospitalization.dischargeDisposition.coding is null
              or not exists (E.hospitalization.dischargeDisposition.coding)
            then false
          else E.hospitalization.dischargeDisposition in "Hospice Disposition"
        )
        and E.status.value in { 'planned', 'arrived', 'in-progress', 'finished', 'onleave' }
  )

/*

define "Medications Indicating End of Life":
  (
    [MedicationAdministration: "End Of Life Opioids"] MA
      where MA.status.value in { 'in-progress', 'on-hold', 'completed' }
  )
  union
  (
    [MedicationDispense: "End Of Life Opioids"] MD
      where MD.status.value in { 'preparation', 'in-progress', 'on-hold', 'completed' }
  )
  union
  (
    [MedicationRequest: "End Of Life Opioids"] MR
      where MR.status.value in { 'active', 'completed' }
  )
  union
  (
    [MedicationStatement: "End Of Life Opioids"] MS
      where MS.status.value in { 'active', 'completed', 'intended' }
  )

*/

define "Previous 10 Days Interval":
Interval[Today() - 10 days, Today()]

define "First Month":
Interval[Today() - 3 months, Today() - 2 months]

define "Second Month":
Interval[Today() - 2 months, Today() - 1 months]

define "Third Month":
Interval[Today() - 1 months, Today()]

define "Prescribed Opioids for 21 or more of 30 Days for each of the past 3 Months":
  "Days on Opioids during Period"("First Month") >= 21
    and "Days on Opioids during Period"("Second Month") >= 21
    and "Days on Opioids during Period"("Third Month") >= 21

define function "Prescription Relevant Period"(prescription FHIR.MedicationRequest):
  if (
    prescription.authoredOn is not null and prescription.dispenseRequest is not null
      and prescription.dispenseRequest.expectedSupplyDuration is not null
  )
  then Interval[
    date from prescription.authoredOn,
    date from prescription.authoredOn + System.Quantity{ value: GetDurationInDays(prescription.dispenseRequest.expectedSupplyDuration), unit: 'days' }
  ]
  else null

define function "Days on Opioids during Period"(period Interval<Date>):
  Sum(
    (
      collapse (
        [MedicationRequest: "Opioid analgesics with ambulatory misuse potential"] OpioidPrescription
        return "Prescription Relevant Period"( OpioidPrescription ) intersect period
      )
    ) OpioidUseInterval
      return days between start of OpioidUseInterval and end of OpioidUseInterval
  )

define function Prescriptions(Orders List<MedicationRequest>):
  Orders O
    let
      // NOTE: Assuming medication is specified as a CodeableConcept with a single RxNorm code
      rxNormCode: FHIRHelpers.ToCode((O.medication as FHIR.CodeableConcept).coding[0]),
      medicationName: OMTKLogic.GetMedicationName(rxNormCode),
      // NOTE: Assuming a single dosage instruction element
      dosageInstruction: O.dosageInstruction[0],
      // NOTE: Assuming a single dose and rate element
      doseAndRate: dosageInstruction.doseAndRate[0],
      repeat: dosageInstruction.timing.repeat,
      frequency: Coalesce(repeat.frequencyMax.value, repeat.frequency.value),
      period: System.Quantity { value: repeat.period.value, unit: repeat.periodUnit.value },
      doseDescription:
        Coalesce(
          // There should be a conversion from FHIR.SimpleQuantity to System.Quantity
          if doseAndRate.dose is FHIR.Range
            then ToString((doseAndRate.dose as FHIR.Range).low)
                + '-' + ToString((doseAndRate.dose as FHIR.Range).high)
                + (doseAndRate.dose as FHIR.Range).high.unit.value
            else ToString(FHIRHelpers.ToQuantity(doseAndRate.dose as FHIR.SimpleQuantity)),
            ''
        ),
      frequencyDescription:
        ToString(dosageInstruction.timing.repeat.frequency.value) +
          Coalesce(
            '-' + ToString(dosageInstruction.timing.repeat.frequencyMax.value),
            ''
          )
    return {
      rxNormCode: rxNormCode,
      isDraft: O.status.value = 'draft',
      // NOTE: Assuming asNeeded is expressed as a boolean
      isPRN: dosageInstruction.asNeeded,
      prescription:
        if dosageInstruction.text is not null then
          medicationName + ' ' + dosageInstruction.text.value
        else
          // TODO: Shouldn't need the .value here on asNeededBoolean
          medicationName + ' ' + doseDescription + ' q' + frequencyDescription + (if dosageInstruction.asNeeded then ' PRN' else ''),
      dose: if doseAndRate.dose is FHIR.Range
            then (doseAndRate.dose as FHIR.Range).high
            else FHIRHelpers.ToQuantity(doseAndRate.dose as FHIR.SimpleQuantity),
      dosesPerDay: Coalesce(OMTKLogic.ToDaily(frequency, period), 1.0)
    }

define function MME(prescriptions List<MedicationRequest>):
  (Prescriptions(prescriptions)) P
    let mme: SingletonFrom(OMTKLogic.CalculateMMEs({ { rxNormCode: P.rxNormCode, doseQuantity: P.dose, dosesPerDay: P.dosesPerDay } }))
    return {
      rxNormCode: P.rxNormCode,
      isDraft: P.isDraft,
      isPRN: P.isPRN,
      prescription: P.prescription,
      dailyDose: mme.dailyDoseDescription,
      conversionFactor: mme.conversionFactor,
      mme: mme.mme
    }

define function TotalMME(prescriptions List<MedicationRequest>):
  System.Quantity {
    value: Sum((MME(prescriptions)) M return M.mme.value),
    unit: 'mg/d'
  }

define function GetDurationInDays(value FHIR.Duration):
  if value is null then null
  else
    case
      when value.code.value ~ 'a' then value.value.value * 365.0
      when value.code.value ~ 'mo' then value.value.value * 30.0
      when value.code.value ~ 'wk' then value.value.value * 7.0
      when value.code.value ~ 'd' then value.value.value
      when value.code.value ~ 'h' then value.value.value / 24.0
      when value.code.value ~ 'min' then value.value.value / 60.0 / 24.0
      when value.code.value ~ 's' then value.value.value / 60.0 / 60.0 / 24.0
      when value.code.value ~ 'ms' then value.value.value / 60.0 / 60.0 / 24.0 / 1000.0
      when value.code.value is null then Message(1000, true, 'Undefined', 'Error', 'Duration unit code is null')
      else Message(1000, true, 'Undefined', 'Error', 'Unsupported duration unit code: ' + value.code.value)
    end

define function GetIngredient(rxNormCode Code):
  OMTKLogic.GetIngredients(rxNormCode).ingredientName

define function GetIngredients(rxNormCodes List<Code>):
  rxNormCodes rnc return GetIngredient(rnc)

define function GetMedicationNames(medications List<MedicationRequest>):
  medications M
    return OMTKLogic.GetMedicationConceptName(M.medication as FHIR.CodeableConcept)

/*
*  Conversion Functions
*/
define function CodeableConceptsToString(concepts List<FHIR.CodeableConcept>):
  concepts c return CodeableConceptToString(c)


define function CodingToString(coding FHIR.Coding):
  if (coding is null)
  then null
  else
    'Code {' &
      'code: ' & coding.code &
      'system: ' & coding.system &
      'version: ' & coding.version &
      'display: ' & coding.display &
    '}'

define function CodeableConceptToString(concept FHIR.CodeableConcept):
  if (concept is null or concept.coding is null)
  then null
  else
    'CodeableConcept {' &
      'Coding: [' &
        Combine(concept.coding Coding return CodingToString(Coding), ',')
      & ']'
    & '}'

define function ToCodes(coding List<FHIR.Coding>):
  coding c return FHIRHelpers.ToCode(c)

define function ToRxNormCode(coding List<FHIR.Coding>):
  singleton from (
    coding C where C.system = 'http://www.nlm.nih.gov/research/umls/rxnorm'
  )
