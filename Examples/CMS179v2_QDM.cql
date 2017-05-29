library CMS179_QDM version '2'

// Warfarin time in therapeutic range

using QDM version '5.0.2'

valueset "Atrial Fibrillation/Flutter": '2.16.840.1.113883.3.117.1.7.1.202'
valueset "Warfarin": '2.16.840.1.113883.3.117.1.7.1.232'
valueset "Face-to-Face Interaction": '2.16.840.1.113883.3.464.1003.101.12.1048'
valueset "Office Visit": '2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "Valvular Heart Disease": '2.16.840.1.113883.3.464.1003.104.12.1017'
valueset "INR": '2.16.840.1.113883.3.117.1.7.1.213'
valueset "birth date": '2.16.840.1.113883.3.560.100.4'
valueset "Computed Value INR percent TTR": '2.16.840.1.113883.3.464.1003.104.12.1018'

parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0))

context Patient

define "FlutterDiagnoses":
	["Diagnosis": "Atrial Fibrillation/Flutter"]

define "WarfarinMedications": 
	["Medication, Active": "Warfarin"]

define "FaceToFaceEncounters": 
	["Encounter, Performed": "Face-to-Face Interaction"]

define "OfficeVisitEncounters": 
	["Encounter, Performed": "Office Visit"]

define "ValvularHeartDiseaseDiagnoses": 
	["Diagnosis": "Valvular Heart Disease"]

define "INRLabResults": 
	["Laboratory Test, Performed": "INR"]

define "BirthDate": 
	["Patient Characteristic Birthdate": "birth date"]

define "ComputedValueINRPercentTTR": 
	["Laboratory Test, Performed": "Computed Value INR percent TTR"]

define "InDemographic":
	AgeInYearsAt(start of MeasurementPeriod) >= 18

define "InpatientEncounters": 
	"FaceToFaceEncounters" 
		union "OfficeVisitEncounters"

define "ActiveFlutterDiagnoses":
	"FlutterDiagnoses" F 
		where F.prevalencePeriod overlaps before MeasurementPeriod

define "ActiveValvularHeartDiseaseDiagnoses":
	"ValvularHeartDiseaseDiagnoses" D 
		where D.prevalencePeriod overlaps before MeasurementPeriod

define "LookbackInterval": 
	Interval[start of MeasurementPeriod - 200 days, start of MeasurementPeriod]

define "ActiveWarfarinDuringLookback":
	"WarfarinMedications" M 
		where M.relevantPeriod overlaps "LookbackInterval"

define "WarfarinUsageIntervals":
	collapse
		"ActiveWarfarinDuringLookback" M
			return M.relevantPeriod intersect "LookbackInterval"

define "WarfarinUsage": 
	Sum("WarfarinUsageIntervals" I return duration in days of I)

define "INROutpatientLabResult":
	"INRLabResults" R
		where not exists (
			"InpatientEncounters" E 
				where duration in hours of E.relevantPeriod > 23 
					and R.relevantPeriod during E.relevantPeriod
		)

define "INRResultsByDay":
	"INROutpatientLabResult" L
		where L.result > 0.8
		return
			Tuple {
				resultDate: date from L.resultDatetime,
				result: if L.result > 10.0 then 10.0 else L.result, // TODO: Units?
				distanceFromMidpoint: Abs(2.5 - L.result) // TODO: Units?
			}

//NOTE: The above Tuple defines an idTag 'result'. Not to be confused with the QDM attribute "result".

define "INRResultsPerDay":
	(
    (distinct "INRResultsByDay" X return X.resultDate) D
	    return First("INRResultsByDay" R where R.resultDate = D sort by R.distanceFromMidpoint)
  ) X
	sort by X.resultDate

define "TherapeuticRange": 
	Interval[2.0, 3.0] // TODO: Units?

define "INRIntervals":
	("INRResultsPerDay" S return Tuple { startResult: S, endResult: First("INRResultsPerDay" E where S.resultDate > E.resultDate) }) X
		return
			Tuple {
				startDate: X.startResult.resultDate,
				endDate: X.endResult.resultDate,
				resultDays: days between X.startResult.resultDate and X.endResult.resultDate,
				resultDifference: X.endResult.result - X.startResult.result,
				resultsWithinBounds: X.startResult.result in "TherapeuticRange" and X.endResult.result in "TherapeuticRange",
				boundedDifference:
					if X.endResult.result >= X.startResult.result
						then (
							if X.startResult.result > end of "TherapeuticRange" or X.endResult.result < start of "TherapeuticRange"
								then null
								else Min({X.endResult.result, end of "TherapeuticRange"}) - Max({X.startResult.result, start of "TherapeuticRange"})
						)
						else (
							if X.endResult.result > end of "TherapeuticRange" or X.startResult.result < start of "TherapeuticRange"
								then null
								else Min({X.startResult.result, end of "TherapeuticRange"}) - Max({X.endResult.result, start of "TherapeuticRange"})
						),
				isValid: days between X.startResult.resultDate and X.endResult.resultDate <= 56
			}

define "TherapeuticDays":
	"INRIntervals" I
		return
			Tuple	{
				startDate: I.startDate,
				endDate : I.endDate,
				isValid : I.isValid,
				resultDays : I.resultDays,
				daysInRange :
					if I.resultsWithinBounds
						then I.resultDays
						else Coalesce(I.resultDays * Abs(I.boundedDifference / (if I.resultDays = 0 then null else I.resultDays)), 0)
			}

define "TherapeuticTimeInRange":
	Round(100 * (Sum("TherapeuticDays" X return X.daysInRange) / Sum("TherapeuticDays" X return X.resultDays)))

define "NumberOfValidIntervals": 
	Count("TherapeuticDays" D where D.isValid)

define "HasValidIntervals": 
	"NumberOfValidIntervals" >= 2

define "InitialPopulation":
	"InDemographic"
		and exists ("InpatientEncounters")
		and exists ("ActiveFlutterDiagnoses")
		and "WarfarinUsage" >= 180
		and not exists ("ActiveValvularHeartDiseaseDiagnoses")

define "MeasurePopulation":
	"HasValidIntervals"

define "MeasureObservation":
	"TherapeuticTimeInRange"

context Population

define "MeasureScore": Avg("MeasureObservation")
