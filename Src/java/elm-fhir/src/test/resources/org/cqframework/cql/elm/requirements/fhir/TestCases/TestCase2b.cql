library TestCase2b

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

parameter "Measurement Period" Interval<DateTime> default Interval[@2019-01-01, @2020-01-01)

context Patient

/*
2b - Bound Measurement Period
Interval[@2019-01-01, @2020-01-01 )
DataRequirement
type: Claim
dateFilter: { path:  item.serviced.start, valuePeriod: "@2019-01-01-@2020-01-01" },
*/

define "HospiceEncounterClaimsBBoundDate":
  [Claim] CEncounter
    where exists CEncounter.item IEncounter
      where IEncounter.serviced."start" in "Measurement Period"
