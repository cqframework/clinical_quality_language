library OpioidCDSREC10 version '1.2.3'

using FHIR version '3.0.0'

include FHIRHelpers version '3.0.0' called FHIRHelpers
include OpioidCDSCommon version '1.2.3' called Common
include OpioidCDSRoutines version '1.2.3' called Routines
include OpioidCDSCommonConfig version '1.2.3' called Config

valueset "Cocaine Medications": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/cocaine-medications'
valueset "PCP Medications": 'http://fhir.org/guides/cdc/opioid-cds/ValueSet/pcp-medications'

/*
**  Recommendation #10
**    When prescribing opioids for chronic pain, providers should use urine drug
**    testing before starting opioid therapy and consider urine drug testing at
**    least annually to assess for prescribed medications as well as other controlled
**    prescription drugs and illicit drugs (recommendation category: B, evidence type: 4)
**
**  When
**    Provider is prescribing an opioid analgesic with ambulatory misuse potential in the outpatient setting
**    Prescription is for treating chronic pain.
**    Opioid review is useful for this patient:
**      Patient is 18 or over
**      Patient does not have findings indicating limited life expectancy
**      Patient does not have orders for therapies indicating end of life care
**      Patient is not undergoing active cancer treatment:
**        Patient has had at least 2 encounters within the past year with any diagnosis of cancer
**    Urine drug screening has not been performed in last 12 months
**  Then
**    Recommend urine drug screening
**      Will perform urine screening
**      Not for chronic pain management, snooze 3 months
**      N/A - see comment, snooze 3 months
**
*/

// META: Plan Definition: http://fhir.org/guides/cdc/opioid-cds/PlanDefinition/opioid-cds-10

parameter ContextPrescriptions List<MedicationRequest>

context Patient

define "Lookback Year":
  Interval[Today() - 12 months - 1 days, Today() - 1 day]

define "PCP Screenings":
[Observation: "category" in Common."Observation Category Laboratory"] LabObservations
 where (date from LabObservations.effective in day of "Lookback Year"
   and (LabObservations.code in "PCP Medications")
   and not (LabObservations.status.value in { 'unknown', 'entered-in-error', 'cancelled' })
)

// Returns a text representation of a dateTime using the CQL `ToString` function.
// @param d - a FHIR dateTime to get text for
// @returns {System.String} the text representation of the dateTime
define function DateTimeText(d FHIR.dateTime):
  ToString(d.value)

define "Positive PCP Screenings":
  "PCP Screenings" PCP where
    StartsWith(Lower(PCP.value as FHIR.string), 'pos')

define "Negative PCP Screenings":
 "PCP Screenings" PCP where
    StartsWith(Lower(PCP.value as FHIR.string), 'neg')

/*
  borrowed from CDS4CPM CDS_Connect_Commons_for_FHIRv400
  Shoudl this go into OpioidCDSCommon?
*/
define function MostRecent(ObsList List<Observation>):
  Last(ObsList O sort by Coalesce(
    (effective as FHIR.dateTime).value,
    (effective as FHIR.Period)."end".value,
    (effective as FHIR.Period)."start".value,
    issued.value)
  )

define "Negative PCP Screenings Count Since Last POS":
  Count(
    "Negative PCP Screenings" N where
      DateTimeText(N.effective) >  DateTimeText("MostRecent"("Positive PCP Screenings").effective)
  )


define "Positive PCP Dates in Lookback Period":
    "PCP Screenings" PS
      return
        DateTimeText(PS.effective)

define "Has Positive Screening for PCP in Last 12 Months":
  exists "PCP Screenings" PCP where
     StartsWith(Lower(PCP.value as FHIR.string), 'pos')

define "PCP Summary":
    'Positive lab dates:   ' + Combine("Positive PCP Dates in Lookback Period", '\r\n') +
    '    Negative test count since last Positive: ' + ToString("Negative PCP Screenings Count Since Last POS")
