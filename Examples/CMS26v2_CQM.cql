library CMS26 version '2'

/* ============================================================================
 * QDM Logic
 * ============================================================================
 * Initial Patient Population =
 *     AND: "Diagnosis, Active: Asthma (ordinality: 'Principal')" starts during "Occurrence A of Encounter, Performed: Encounter Inpatient"
 *     AND: "Patient Characteristic Birthdate: birth date" >= 2 year(s) starts before start of "Occurrence A of Encounter, Performed: Encounter Inpatient"
 *     AND: "Patient Characteristic Birthdate: birth date" <= 17 year(s) starts before start of "Occurrence A of Encounter, Performed: Encounter Inpatient"
 *     AND: "Occurrence A of Encounter, Performed: Encounter Inpatient (length of stay <= 120 day(s))"
 *     AND: "Occurrence A of Encounter, Performed: Encounter Inpatient" ends during "Measurement Period"
 *
 * Denominator =
 *     AND: "Initial Patient Population"
 *     AND: "Occurrence A of Encounter, Performed: Encounter Inpatient (discharge status: 'Discharge To Home Or Police Custody')"
 *
 * Denominator Exclusions =
 *     None
 *
 * Numerator =
 *     AND:
 *         OR: "Communication: From Provider to Patient: Asthma Management Plan"
 *         OR: "Communication: From Provider to Patient not done: Patient Refusal" for "Asthma Management Plan"
 *         during "Occurrence A of Encounter, Performed: Encounter Inpatient"
 *
 * Denominator Exceptions =
 *     None
 * ============================================================================
 */

using QUICK

valueset "Asthma": '2.16.840.1.113883.3.117.1.7.1.271'
valueset "Asthma Management Plan": '2.16.840.1.113883.3.117.1.7.1.131'
valueset "Home Discharge Disposition": 'TBD'
valueset "Doctor": 'TBD'

parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0))

context Patient

define "In Demographic":
  AgeInYearsAt(start of MeasurementPeriod) >= 2 and AgeInYearsAt(start of MeasurementPeriod) <= 17

define "Asthma Encounters":
  ["Encounter": "Asthma"] E
    where E."class" = 'inpatient'
      and E."length" <= 120 days
      and E."hospitalization"."dischargeDisposition" in "Home Discharge Disposition"
      and E."period" ends during MeasurementPeriod

define "Asthma Encounters with Plan":
  "Asthma Encounters" E
    with ["CommunicationRequest"] C
      such that (C."encounter" as "Encounter")."id" = E."id"
        and exists ((C."reason") R where R in "Asthma Management Plan")
        and exists (((C."sender" as "Practitioner")."practitionerRole") R where R."role" in "Doctor")
        and (First(C."recipient") as "Patient")."id" = (C."subject" as "Patient")."id"

define "In Initial Patient Population":
    "In Demographic" and exists ("Asthma Encounters")

context Population

define "Denominator": "Asthma Encounters"

define "Numerator": "Asthma Encounters with Plan"
