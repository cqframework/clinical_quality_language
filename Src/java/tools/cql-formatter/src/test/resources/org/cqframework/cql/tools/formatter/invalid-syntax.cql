library test version '0.0.000'

using QDM

valueset "Female Administrative Sex": '2.16.840.1.113883.3.560.100.2'
valueset "TestValueset": '1.1.111'

parameter "test param" Interval<DateTime

context Patient

define "TestDefinition":
	["Encounter, Performed": "TestValueset "]