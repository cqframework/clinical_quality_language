library TestSignature

using FHIR version '4.0.1'

include WHOCommon called WC
include FHIRHelpers version '4.0.1'

define "TestData":
  Encounter { id: FHIR.id { value: '123' } }


define "TestDefine":
   WC.ModifierExtension("TestData", 'who-notDone')

define "Co-habitants":
  [Observation] O
    where O.status in { 'final', 'amended', 'corrected' }
      and Coalesce(WC.ModifierExtension(O, 'who-notDone').value, false) is false