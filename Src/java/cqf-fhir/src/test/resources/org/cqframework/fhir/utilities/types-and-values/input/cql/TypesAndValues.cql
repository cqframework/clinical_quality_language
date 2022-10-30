library TypesAndValues version '3.0.000'

using FHIR version '4.0.1'

include fhir.cqf.common.FHIRHelpers version '4.0.1' called FHIRHelpers
include fhir.cqf.common.FHIRCommon version '4.0.1' called FHIRCommon

valueset "Office Visit": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1001'

parameter "Measurement Period" Interval<DateTime>
parameter X Choice<Integer, Decimal, String, Quantity>
parameter ErrorSeverity String default 'Warning'

context Patient

define "Qualifying Encounters":
  [Encounter: "Office Visit"] ValidEncounter
	where ValidEncounter.status  = 'finished'
	  and FHIRCommon.ToInterval(ValidEncounter.period) during day of "Measurement Period"


define Info: Tuple { name: 'Patrick', birthDate: @2014-01-01 }
define PatientExpression: Patient { name: { FHIR.HumanName { given: { FHIR.string { value: 'Patrick' } } } }, birthDate: FHIR.date { value: @2014-01-01 } }

define IntegerInterval: Interval[3, 5)
define DateTimeInterval: Interval[@2014-01-01T00:00:00.0, @2015-01-01T00:00:00.0)

define IntegerList: { 1, 2, 3, 4, 5 }
define StringList: { 'a', 'b', 'c' }

define TestXAsInteger: X = 1
define TestXAsDecimal: X = 1.0
define TestXAsString: X = 'ABC'
define TestXAsQuantity: X = 20 'mg'
// Author-time error because Boolean is not one of the possible types for the parameter X
//define TestXAsBoolean: X = true


// Explicit Conversions: https://cql.hl7.org/03-developersguide.html#explicit-conversion
define ConvertValues: convert '1' to Integer
define ConvertUnits: convert 5 'mg' to 'g'
define InvalidConvert: convert 'Foo' to Integer // Results in null

// Implicit Conversions: https://cql.hl7.org/03-developersguide.html#implicit-conversions
define ImplicitConversion: 2 + 2.0 // Evaluates as Decimal addition, the integer input is implicitly converted to a decimal

/*
@description: Returns the value of the given Quantity, interpreted as an Integer if it is safe to do so.
@comments: A Quantity value can be safely interpreted as an integer if it has no decimal component (i.e. zeros after the decimal),
and it has the default UCUM unit of '1'
*/
define function ToInteger(value Quantity):
  case
    when Abs(value.value - Truncate(value.value)) > 0.00000001 then 
      Message(null, true, 'ToInteger.InvalidArgument', ErrorSeverity, 'The quantity has a non-zero decimal component and cannot be safely interpreted as an Integer')
    when value.unit != '1' then
      Message(null, true, 'ToInteger.InvalidUnit', ErrorSeverity, 'The quantity has non-default units specified and cannot be safely interpreted as an Integer')
    else
      Truncate(value.value)
  end

/*
@description: Returns the value, interpreted as an Integer
@comments: If the input is a Quantity, the result is the same as invoking `ToInteger(value as Quantity)`.
If the input is an Integer, the result is the same as invoking `value as Integer`
*/
define function ToInteger(value Choice<Integer, Quantity>):
  if value is Quantity then 
    ToInteger(value as Quantity)
  else 
    value as Integer

