library VTEICU_QICore4 version '4.0.000'

/*
@update: @@BTR 2020-03-31 ->
Incremented version to 5.0.000
Updated FHIR version to 4.0.1
@@
*/

using QICore version '4.1.1'

include FHIRHelpers version '4.0.1'

// NOTE: BTR 2019-07-30: Updated dependencies
include MATGlobalCommonFunctions_QICore4 version '5.0.000' called Global

valueset "Intensive Care Unit": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113762.1.4.1110.23'

parameter "Measurement Period" Interval<DateTime>
  default Interval[@2019-01-01T00:00:00.0, @2020-01-01T00:00:00.0)

context Patient

define function "FromDayOfStartOfHospitalizationToDayAfterAdmission"(Encounter Encounter ):
	Interval[Global."ToDate"(start of Global."HospitalizationWithObservation"(Encounter)), Global."ToDate"(start of Encounter.period + 2 days))

define function "StartOfFirstICU"(Encounter Encounter ):
	start of "FirstICULocationPeriod"(Encounter)

define function "FromDayOfStartOfHospitalizationToDayAfterFirstICU"(Encounter Encounter ):
	Interval[Global."ToDate"(start of Global."HospitalizationWithObservation"(Encounter)), Global."ToDate"(StartOfFirstICU(Encounter)+ 2 days))

define function "FirstICULocationPeriod"(Encounter Encounter ):
	"FirstInpatientIntensiveCareUnit"(Encounter).period

define function "FirstInpatientIntensiveCareUnit"(Encounter Encounter ):
	First((Encounter.location)HospitalLocation
			where Global.GetLocation(HospitalLocation.location).type in "Intensive Care Unit"
				and HospitalLocation.period during Encounter.period
			sort by start of period
	)
