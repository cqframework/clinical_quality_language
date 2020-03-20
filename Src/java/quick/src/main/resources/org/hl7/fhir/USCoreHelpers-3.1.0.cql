library USCoreHelpers version '3.1.0'

using FHIR version '4.0.1'

define function ToInterval(period FHIR.Period):
    if period is null then
        null
    else
        Interval[period."start".value, period."end".value]

define function ToQuantity(quantity FHIR.Quantity):
    if quantity is null then
        null
    else
        System.Quantity { value: quantity.value.value, unit: quantity.unit.value }

define function ToRatio(ratio FHIR.Ratio):
    if ratio is null then
        null
    else
        System.Ratio { numerator: ToQuantity(ratio.numerator), denominator: ToQuantity(ratio.denominator) }

define function ToInterval(range FHIR.Range):
    if range is null then
        null
    else
        Interval[ToQuantity(range.low), ToQuantity(range.high)]

define function ToCode(coding FHIR.Coding):
    if coding is null then
        null
    else
        System.Code {
          code: coding.code.value,
          system: coding.system.value,
          version: coding.version.value,
          display: coding.display.value
        }

define function ToConcept(concept FHIR.CodeableConcept):
    if concept is null then
        null
    else
        System.Concept {
            codes: concept.coding C return ToCode(C),
            display: concept.text.value
        }


