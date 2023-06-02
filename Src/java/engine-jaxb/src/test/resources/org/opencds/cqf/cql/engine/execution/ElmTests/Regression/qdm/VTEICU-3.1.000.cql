library VTEICU version '3.1.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global

valueset "Intensive Care Unit": 'urn:oid:2.16.840.1.113762.1.4.1029.206' 

parameter "Measurement Period" Interval<DateTime>

context Patient

define function "FromDayOfStartOfHospitalizationToDayAfterAdmission"(Encounter "Encounter, Performed" ):
	Interval[Global."ToDate"(start of Global."HospitalizationWithObservation"(Encounter)), Global."ToDate"(start of Encounter.relevantPeriod + 2 days))

define function "StartOfFirstICU"(Encounter "Encounter, Performed" ):
	start of "FirstICULocationPeriod"(Encounter)

define function "FromDayOfStartOfHospitalizationToDayAfterFirstICU"(Encounter "Encounter, Performed" ):
	Interval[Global."ToDate"(start of Global."HospitalizationWithObservation"(Encounter)), Global."ToDate"(StartOfFirstICU(Encounter)+ 2 days))

define function "FirstICULocationPeriod"(Encounter "Encounter, Performed" ):
	"FirstInpatientIntensiveCareUnit"(Encounter).locationPeriod

define function "FirstInpatientIntensiveCareUnit"(Encounter "Encounter, Performed" ):
	First((Encounter.facilityLocations)HospitalLocation
			where HospitalLocation.code in "Intensive Care Unit"
				and HospitalLocation.locationPeriod during Encounter.relevantPeriod
			sort by start of locationPeriod
	)

