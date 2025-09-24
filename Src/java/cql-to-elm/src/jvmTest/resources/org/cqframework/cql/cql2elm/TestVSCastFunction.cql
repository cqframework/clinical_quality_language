library TestVSCastFunction

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

valueset "Narcolepsy": 'https://example.org/fhir/ValueSet/narcolepsy'

define function "VS Cast Function"(VSet List<System.Code>):
  ( ( cast { "VSet", 1 }[0]as Tuple {
      codes List<System.Code>,
      oid System.String,
      version System.String
    }
  ).codes ) VSetCodes
    return System.Code { code: VSetCodes.code, system: VSetCodes.system }

define function "Conditions in ValueSet"(conditions List<Condition>, codes List<System.Code>):
  conditions C
    where FHIRHelpers.ToConcept(C.code) in codes

define TestConditions:
  [Condition] C
    where C.code in "Narcolepsy"

define TestConditionsViaFunction:
  "Conditions in ValueSet"([Condition], "VS Cast Function"("Narcolepsy"))

define TestConditionsDirectly:
  "Conditions in ValueSet"([Condition], "Narcolepsy")
