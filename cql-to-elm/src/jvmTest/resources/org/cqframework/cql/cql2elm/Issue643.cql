library Issue643

/*
https://github.com/cqframework/clinical_quality_language/issues/643
This library demonstrates ValueSet reference semantics that should be resolving but are not:
 */

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

valueset "Test Value Set": 'TBD'

define function EncountersWithCoding(encounters List<Encounter>, valueSet System.ValueSet):
  encounters E
    where E.class in valueSet

define function EncountersWithType(encounters List<Encounter>, valueSet System.ValueSet):
  encounters E
    where E.type in valueSet

define function EncountersWithServiceType(encounters List<Encounter>, valueSet System.ValueSet):
  encounters E
    where E.serviceType in valueSet


define EncountersWithCoding: EncountersWithCoding([Encounter], "Test Value Set")

define EncountersWithType: EncountersWithType([Encounter], "Test Value Set")

define EncountersWithServiceType: EncountersWithServiceType([Encounter], "Test Value Set")