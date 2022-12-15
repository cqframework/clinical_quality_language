library TestFHIRHelpersDstu2 version '0.1.0'

using FHIR version '1.0.2'

include FHIRHelpers version '1.0.2' called FHIRHelpers

define TestPeriodToInterval: FHIRHelpers.ToInterval(FHIR.Period { "start": FHIR.dateTime { value: @2017-05-06T18:08:00 }, "end": FHIR.dateTime { value: @2017-05-06T19:08:00 } })
define TestToQuantity: FHIRHelpers.ToQuantity(FHIR.Quantity { value: FHIR.decimal { value: 1.0 }, unit: FHIR.string { value: 'mcg/h' } })
define TestRangeToInterval: FHIRHelpers.ToInterval(FHIR.Range { low: FHIR.SimpleQuantity { value: FHIR.decimal { value: 1.0 }, unit: FHIR.string { value: 'mcg/h' } }, high: FHIR.SimpleQuantity { value: FHIR.decimal { value: 2.0 }, unit: FHIR.string { value: 'mcg/h' } } })
define TestToConcept: FHIRHelpers.ToConcept(FHIR.CodeableConcept { coding: { FHIR.Coding { code: FHIR.code { value: '123' }, system: FHIR.uri { value: 'http://loinc.org' }, "version": FHIR.string { value: '201601' }, "display": FHIR.string { value: 'the code' } } }, text: FHIR.string { value: 'the code' } })
define TestToCode: FHIRHelpers.ToCode(FHIR.Coding { code: FHIR.code { value: '123' }, system: FHIR.uri { value: 'http://loinc.org' }, "version": FHIR.string { value: '201601' }, "display": FHIR.string { value: 'the code' } })
define TestToString: FHIRHelpers.ToString(FHIR.uuid { value: '1234567' })
define TestRequestStatusToString: FHIRHelpers.ToString(FHIR.MedicationOrderStatus { value: 'active' })
define TestToDateTime: FHIRHelpers.ToDateTime(FHIR.dateTime { value: @2017-05-06T18:08:00 })
define TestToTime: FHIRHelpers.ToTime(FHIR.time { value: @T18:08:00 })
define TestToInteger: FHIRHelpers.ToInteger(FHIR.integer { value: 1 })
define TestToDecimal: FHIRHelpers.ToDecimal(FHIR.decimal { value: 1.0 })
define TestToBoolean: FHIRHelpers.ToBoolean(FHIR.boolean { value: true })

