library QICoreHelpers version '4.0.0'

using FHIR version '4.0.1'

define function ToInterval(period FHIR.Period):
    if period is null then
        null
    else
        if period."start" is null then
            Interval(period."start".value, period."end".value]
        else
            Interval[period."start".value, period."end".value]

define function ToCalendarUnit(unit System.String):
    case unit
        when 'ms' then 'millisecond'
        when 's' then 'second'
        when 'min' then 'minute'
        when 'h' then 'hour'
        when 'd' then 'day'
        when 'wk' then 'week'
        when 'mo' then 'month'
        when 'a' then 'year'
        else unit
    end

define function ToQuantity(quantity FHIR.Quantity):
    case
        when quantity is null then null
        when quantity.value is null then null
        when quantity.comparator is not null then
            Message(null, true, 'FHIRHelpers.ToQuantity.ComparatorQuantityNotSupported', 'Error', 'FHIR Quantity value has a comparator and cannot be converted to a System.Quantity value.')
        when quantity.system is null or quantity.system.value = 'http://unitsofmeasure.org'
              or quantity.system.value = 'http://hl7.org/fhirpath/CodeSystem/calendar-units' then
            System.Quantity { value: quantity.value.value, unit: ToCalendarUnit(Coalesce(quantity.code.value, quantity.unit.value, '1')) }
        else
            Message(null, true, 'FHIRHelpers.ToQuantity.InvalidFHIRQuantity', 'Error', 'Invalid FHIR Quantity code: ' & quantity.unit.value & ' (' & quantity.system.value & '|' & quantity.code.value & ')')
    end

define function ToQuantityIgnoringComparator(quantity FHIR.Quantity):
    case
        when quantity is null then null
        when quantity.value is null then null
        when quantity.system is null or quantity.system.value = 'http://unitsofmeasure.org'
              or quantity.system.value = 'http://hl7.org/fhirpath/CodeSystem/calendar-units' then
            System.Quantity { value: quantity.value.value, unit: ToCalendarUnit(Coalesce(quantity.code.value, quantity.unit.value, '1')) }
        else
            Message(null, true, 'FHIRHelpers.ToQuantity.InvalidFHIRQuantity', 'Error', 'Invalid FHIR Quantity code: ' & quantity.unit.value & ' (' & quantity.system.value & '|' & quantity.code.value & ')')
    end

define function ToInterval(quantity FHIR.Quantity):
    if quantity is null then null else
        case quantity.comparator.value
            when '<' then
                Interval[
                    null,
                    ToQuantityIgnoringComparator(quantity)
                )
            when '<=' then
                Interval[
                    null,
                    ToQuantityIgnoringComparator(quantity)
                ]
            when '>=' then
                Interval[
                    ToQuantityIgnoringComparator(quantity),
                    null
                ]
            when '>' then
                Interval(
                    ToQuantityIgnoringComparator(quantity),
                    null
                ]
            else
                Interval[ToQuantity(quantity), ToQuantity(quantity)]
        end

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


