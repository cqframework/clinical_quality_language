library CMS146 version '2'

/* CMS 146v2
 *
 * ============================================================================
 * QDM Logic
 * ============================================================================
 * Initial Patient Population =
 *     AND: "Patient Characteristic Birthdate: birth date" >= 2 year(s) starts before start of "Measurement Period"
 *     AND: "Patient Characteristic Birthdate: birth date" < 18 year(s) starts before start of "Measurement Period"
 *     AND:
 *         AND: "Occurrence A of Encounter, Performed: Ambulatory/ED Visit" during "Measurement Period"
 *         AND: "Medication, Order: Antibiotic Medications" <= 3 day(s) starts after start of "Occurrence A of Encounter, Performed: Ambulatory/ED Visit"
 *         AND:
 *             OR: "Occurrence A of Encounter, Performed: Ambulatory/ED Visit" during
 *                 OR: "Occurrence A of Diagnosis, Active: Acute Pharyngitis"
 *                 OR: "Occurrence A of Diagnosis, Active: Acute Tonsillitis"
 *             OR:
 *                 OR: "Occurrence A of Diagnosis, Active: Acute Pharyngitis"
 *                 OR: "Occurrence A of Diagnosis, Active: Acute Tonsillitis"
 *                 starts during "Occurrence A of Encounter, Performed: Ambulatory/ED Visit"
 *
 * Denominator =
 *     AND: "Initial Patient Population"
 *
 * Denominator Exclusions =
 *     AND: "Medication, Active: Antibiotic Medications" <= 30 day(s) starts before start of
 *         OR: "Occurrence A of Diagnosis, Active: Acute Pharyngitis"
 *         OR: "Occurrence A of Diagnosis, Active: Acute Tonsillitis"
 *
 * Numerator =
 *     AND:
 *         OR: "Laboratory Test, Result: Group A Streptococcus Test (result)" <= 3 day(s) starts before or during "Occurrence A of Encounter, Performed: Ambulatory/ED Visit"
 *         OR: "Laboratory Test, Result: Group A Streptococcus Test (result)" <= 3 day(s) starts after end of "Occurrence A of Encounter, Performed: Ambulatory/ED Visit"
 *
 * Denominator Exceptions =
 *     None
 * ============================================================================
 */

using QUICK

parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))

define "Acute Pharyngitis" = ValueSet('2.16.840.1.113883.3.464.1003.102.12.1011')
define "Acute Tonsillitis" = ValueSet('2.16.840.1.113883.3.464.1003.102.12.1012')
define "Ambulatory/ED Visit" = ValueSet('2.16.840.1.113883.3.464.1003.101.12.1061')
define "Antibiotic Medications" = ValueSet('2.16.840.1.113883.3.464.1003.196.12.1001')
define "Group A Streptococcus Test" = ValueSet('2.16.840.1.113883.3.464.1003.198.12.1012')

context PATIENT

define InDemographic =
    AgeAt(start of MeasurementPeriod) >= 2 and AgeAt(start of MeasurementPeriod) < 18

define Pharyngitis =
    ([Condition: "Acute Pharyngitis"]) union ([Condition: "Acute Tonsilitis"])

define Antibiotics =
    [MedicationPrescription: "Antibiotic Medications"]

define TargetEncounters =
    [Encounter, Performance: "Ambulatory/ED Visit"] E
        with Pharyngitis P such that P.effectiveTime overlaps after E.performedAtTime
        with Antibiotics A such that A.orderedAtTime starts at most 3 days after start E.performedAtTime
        where E.performedAtTime during MeasurementPeriod

define TargetDiagnoses =
    Pharyngitis P with TargetEncounters E where P.effectiveTime overlaps after E.performedAtTime

define HasPriorAntibiotics =
    exists (Antibiotics A with TargetDiagnoses D such that A.orderedAtTime starts at most 30 days before start D.effectiveTime)

define HasTargetEncounter =
    exists (TargetEncounters)

define InInitialPopulation =
    InDemographic and HasTargetEncounter

define InDenominator =
    true

define InDenominatorExclusions =
    HasPriorAntibiotics

define InNumerator =
    exists ([ObservationResult: "Group A Streptococcus Test"] R where R.observedAtTime during MeasurementPeriod and R.result is not null)
