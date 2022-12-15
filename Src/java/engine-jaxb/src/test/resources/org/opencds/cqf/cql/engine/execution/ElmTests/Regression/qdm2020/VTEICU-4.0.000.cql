library VTEICU version '4.0.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global

parameter "Measurement Period" Interval<DateTime>

context Patient

define function "FromDayOfStartOfHospitalizationToDayAfterAdmission"(Encounter "Encounter, Performed" ):
  Interval[Global."ToDate"(start of Global."HospitalizationWithObservation"(Encounter)), Global."ToDate"(start of Encounter.relevantPeriod + 2 days))

define function "StartOfFirstICU"(Encounter "Encounter, Performed" ):
  start of "FirstICULocationPeriod"(Encounter)

define function "FromDayOfStartOfHospitalizationToDayAfterFirstICU"(Encounter "Encounter, Performed" ):
  Interval[Global."ToDate"(start of Global."HospitalizationWithObservation"(Encounter)), Global."ToDate"(StartOfFirstICU(Encounter)+ 2 days))

define function "FirstICULocationPeriod"(Encounter "Encounter, Performed" ):
  Global."FirstInpatientIntensiveCareUnit" ( Encounter ).locationPeriod

