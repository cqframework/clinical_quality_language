library CMS146_QDM_MS version '2'

/*
 * This take differs from the original in that it uses one multi-source query
 * instead of multiple single-source queries.
 */

using QDM // version '4.2'

valueset "Acute Pharyngitis": '2.16.840.1.113883.3.464.1003.102.12.1011'
valueset "Acute Tonsillitis": '2.16.840.1.113883.3.464.1003.102.12.1012'
valueset "Ambulatory/ED Visit": '2.16.840.1.113883.3.464.1003.101.12.1061'
valueset "Antibiotic Medications": '2.16.840.1.113883.3.464.1003.196.12.1001'
valueset "Group A Streptococcus Test": '2.16.840.1.113883.3.464.1003.198.12.1012'

parameter MeasurementPeriod Interval<DateTime>

context Patient

define "InDemographic":
    AgeInYearsAt(start of MeasurementPeriod) >= 2 and AgeInYearsAt(start of MeasurementPeriod) < 18

define "EncounterData":
    from
        ["EncounterPerformed": "Ambulatory/ED Visit"] E,
        (["Diagnosis": "Acute Pharyngitis"] union ["Diagnosis": "Acute Tonsillitis"]) P,
        ["MedicationOrder": "Antibiotic Medications"] A,
        ["LaboratoryTestPerformed": "Group A Streptococcus Test"] T
    define
        InMP : Interval[E."admissionDatetime", E."dischargeDatetime"] during MeasurementPeriod,
        PharyngitisDx : Interval[P."onsetDatetime", P."abatementDatetime"] includes Interval[E."admissionDatetime", E."dischargeDatetime"]
            or P."onsetDatetime" in Interval[E."admissionDatetime", E."dischargeDatetime"],
        PrescribedAntibiotics : A."signedDatetime" 3 days or less after E."admissionDatetime"
    where
        InDemographic and InMP and PharyngitisDx and PrescribedAntibiotics 
        and E."negationRationale" is not null
        and A."negationRationale" is not null
        and T."negationRationale" is not null
    return Tuple {
        Encounter: E,
        PriorAntibiotics: A."signedDatetime" 30 days or less before P."onsetDatetime",
        StrepTest: T.result is not null
            and T."startDatetime" in Interval[E."admissionDatetime" - 3 days, E."dischargeDatetime" + 3 days]
    }

define "IPPCount": Count (
    "EncounterData" E return E.Encounter
)

define "DenominatorCount": "IPPCount"

define "DenominatorExclusionsCount": Count (
    "EncounterData" E
        where E.PriorAntibiotics
        return E.Encounter
)

define "NumeratorCount": Count (
    "EncounterData" E
        where E.StrepTest
            and (not E.PriorAntibiotics or E.PriorAntibiotics is null)
        return E.Encounter
)
