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

using QLIM

context PATIENT

parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))

concept "Acute Pharyngitis" = ValueSet("2.16.840.1.113883.3.464.1003.102.12.1011")
concept "Acute Tonsillitis" = ValueSet("2.16.840.1.113883.3.464.1003.102.12.1012")
concept "Ambulatory/ED Visit" = ValueSet("2.16.840.1.113883.3.464.1003.101.12.1061")
concept "Antibiotic Medications" = ValueSet("2.16.840.1.113883.3.464.1003.196.12.1001")
concept "Group A Streptococcus Test" = ValueSet("2.16.840.1.113883.3.464.1003.198.12.1012")

let InDemographic =
	AgeAt(start of MeasurementPeriod) >= 2 and AgeAt(start of MeasurementPeriod) < 18

let Pharyngitis =
    union ([Condition: "Acute Pharyngitis"], [Condition: "Acute Tonsilitis"])

let Antibiotics =
    [MedicationPrescription: "Antibiotic Medications"]

let TargetEncounters =
    [Encounter, Performed: "Ambulatory/ED Visit"] 
		where performedAtTime during MeasurementPeriod
	        and Pharyngitis.effectiveTime overlaps after performedAtTime
		    and Antibiotics.orderedAtTime starts within 3 days after start performedAtTime

let TargetEncounters =
	[Encounter, Performed: "Ambulatory/ED Visit"]
		where performedAtTime during MeasurementPeriod
			and exists (Pharyngitis where Pharyngitis.effectiveTime overlaps after performedAtTime)
			and exists (Antibiotics where Antibiotics.orderedAtTime starts within 3 days after start performedAtTime)

let TargetEncounters =
    [Encounter, Performed: "Ambulatory/ED Visit"] 
		where performedAtTime during MeasurementPeriod
		with Pharyngitis where Pharyngitis.effectiveTime overlaps after performedAtTime
		with Antibiotics where Antibiotics.orderedAtTime starts within 3 days after start performedAtTime

let TargetDiagnoses =
    Pharyngitis where effectiveTime overlaps after TargetEncounters.performedAtTime

let TargetDiagnoses =
	Pharyngitis where exists (TargetEncounters where Pharyngitis.effectiveTime overlaps after TargetEncounters.performedAtTime)

let TargetDiagnoses =
	Pharyngitis with TargetEncounters where Pharyngitis.effectiveTime overlaps after TargetEncounters.performedAtTime

let HasPriorAntibiotics =
    exists (Antibiotics where orderedAtTime starts within 30 days before start TargetDiagnoses.effectiveTime)

let HasPriorAntibiotics =
	exists (Antibiotics where exists (TargetDiagnoses where Antibiotics.orderedAtTime starts within 30 days before start TargetDiagnoses.effectiveTime))

let HasPriorAntibiotics =
	exists (
		Antibiotics with TargetDiagnoses 
			where Antibiotics.orderedAtTime starts within 30 days before start TargetDiagnoses.effectiveTime 
	)

let HasTargetEncounter =
    exists (TargetEncounters)

let InInitialPopulation =
	InDemographic and HasTargetEncounter

let InDenominator =
    InInitialPopulation

let InDenominatorExclusions =
    InDenominator and HasPriorAntibiotics

let InNumerator =
    InDenominator
        and not InDenominatorExclusions
        and exists ([ObservationResult: "Group A Streptococcus Test"] where observedAtTime during MeasurementPeriod)

