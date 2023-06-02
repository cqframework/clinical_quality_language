library SafeUseofOpioidsConcurrentPrescribing version '3.3.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "All Primary and Secondary Cancer": 'urn:oid:2.16.840.1.113762.1.4.1111.161'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Palliative or Hospice Care": 'urn:oid:2.16.840.1.113883.3.600.1.1579'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Schedule II and III Opioid Medications": 'urn:oid:2.16.840.1.113762.1.4.1111.165'
valueset "Schedule IV Benzodiazepines": 'urn:oid:2.16.840.1.113762.1.4.1125.1'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'

context Patient

define "Denominator":
  "Initial Population"

define "Inpatient Encounter with Age Greater than or Equal to 18":
  Global."Inpatient Encounter" InpatientHospitalEncounter
    with ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      such that Global.CalendarAgeInYearsAt ( BirthDate.birthDatetime, start of InpatientHospitalEncounter.relevantPeriod ) >= 18

define "Numerator":
  /*Encounters of patients prescribed two or more opioids or an opioid and benzodiazepine at discharge.
  */
  ( "Inpatient Encounter with Age Greater than or Equal to 18" InpatientEncounter
      where ( Count(["Medication, Discharge": "Schedule II and III Opioid Medications"] Opioids
            where Opioids.authorDatetime during InpatientEncounter.relevantPeriod
        )>= 2
      )
  )
    union ( "Inpatient Encounter with Age Greater than or Equal to 18" InpatientEncounter
        with ["Medication, Discharge": "Schedule II and III Opioid Medications"] OpioidsDischarge
          such that OpioidsDischarge.authorDatetime during InpatientEncounter.relevantPeriod
        with ["Medication, Discharge": "Schedule IV Benzodiazepines"] BenzodiazepinesDischarge
          such that BenzodiazepinesDischarge.authorDatetime during InpatientEncounter.relevantPeriod
    )

define "SDE Ethnicity":
  ["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
  ["Patient Characteristic Payer": "Payer"]

define "SDE Race":
  ["Patient Characteristic Race": "Race"]

define "SDE Sex":
  ["Patient Characteristic Sex": "ONC Administrative Sex"]

define "Denominator Exclusion":
  /*Excludes patients with cancer or who are receiving palliative or hospice care at the time of the encounter*/
  "Inpatient Encounter with Age Greater than or Equal to 18" InpatientEncounter
    where exists ( ["Diagnosis": "All Primary and Secondary Cancer"] Cancer
        where Cancer.prevalencePeriod overlaps InpatientEncounter.relevantPeriod
    )
      or exists ( InpatientEncounter.diagnoses Diagnosis
          where Diagnosis.code in "All Primary and Secondary Cancer"
      )
      or exists ( ["Intervention, Order": "Palliative or Hospice Care"] PalliativeOrHospiceCareOrder
          where PalliativeOrHospiceCareOrder.authorDatetime during InpatientEncounter.relevantPeriod
      )
      or exists ( ["Intervention, Performed": "Palliative or Hospice Care"] PalliativeOrHospiceCarePerformed
          where PalliativeOrHospiceCarePerformed.relevantPeriod overlaps InpatientEncounter.relevantPeriod
      )

define "Initial Population":
  /*Captures encounters of patients with an opioid(s), benzodiazepine, or a combination of these medications at discharge*/
  "Inpatient Encounter with Age Greater than or Equal to 18" InpatientEncounter
    with ( ["Medication, Discharge": "Schedule II and III Opioid Medications"]
      union ["Medication, Discharge": "Schedule IV Benzodiazepines"] ) OpioidOrBenzodiazepineDischargeMedication
      such that OpioidOrBenzodiazepineDischargeMedication.authorDatetime during InpatientEncounter.relevantPeriod