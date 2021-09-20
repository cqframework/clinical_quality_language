library TestCase2a

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

valueset "Hospice Encounter": 'http://fakeurl.com/hospice-encounter'

context Patient

/*
2a
DataRequirement
type: Claim
codeFilter: { path:  item.revenue, valueSet: 'http://fakeurl.com/hospice-encounter' }
*/

define "HospiceEncounterClaimsA":
  [Claim] CEncounter
    where exists CEncounter.item IEncounter
      where IEncounter.revenue in "Hospice Encounter"

