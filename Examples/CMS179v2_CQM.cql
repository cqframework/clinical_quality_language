library CMS179 version '2'

// Warfarin time in therapeutic range

using QUICK

parameter MeasurementPeriod default interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0))

valueset "Flutter Diagnosis" = '2.16.840.1.113883.3.117.1.7.1.202'
valueset "Warfarin Medication" = '2.16.840.1.113883.3.117.1.7.1.232'
valueset "Face-to-face Encounter" = '2.16.840.1.113883.3.464.1003.101.12.1048'
valueset "Office Visit" = '2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "Valvular Heart Disease" = '2.16.840.1.113883.3.464.1003.104.12.1017'
valueset "INR Lab Result" = '2.16.840.1.113883.3.117.1.7.1.213'

context Patient

define FlutterDiagnoses = [Condition: "Flutter Diagnosis"]
define WarfarinMedications = [MedicationAdministration: "Warfarin Medication"]
define FaceToFaceEncounters = [Encounter: "Face-to-face Encounter"]
define OfficeVisitEncounters = [Encounter: "Office Visit"]
define ValvularHeartDiseaseDiagnoses = [Condition: "Valvular Heart Disease"]
define INRLabResults = [Observation: "INR Lab Result"]

define InDemographic =
	AgeInYearsAt(start of MeasurementPeriod) >= 18

define InpatientEncounters = FaceToFaceEncounters union OfficeVisitEncounters
define ActiveFlutterDiagnoses = FlutterDiagnoses F where interval[F.onsetDate, F.abatementDate] overlaps before MeasurementPeriod
define ActiveValvularHeartDiseaseDiagnoses = ValvularHeartDiseaseDiagnoses D where interval[D.onsetDate, D.abatementDate] overlaps before MeasurementPeriod

define LookbackInterval = interval[start of MeasurementPeriod - 200 days, start of MeasurementPeriod]

define ActiveWarfarinDuringLookback =
	WarfarinMedications M where M.administeredTime overlaps LookbackInterval

define WarfarinUsageIntervals =
	collapse 
		ActiveWarfarinDuringLookback M 
			return tuple { usageWithinLookback: M.administeredTime intersect LookbackInterval }

define WarfarinUsage = Sum(WarfarinUsageIntervals I return duration in days of I)

define INROutpatientLabResult =
	INRLabResults R 
		where not exists (InpatientEncounters E where duration in hours of E.period > 23 and R.diagnosticDateTime starts during E.period)

define INRResultsByDay =
	INROutpatientLabResult L 
		where L.result > 0.8 // TODO: Units?
		return 
			tuple 
			{ 
				resultDate: date from L.diagnosticDateTime,
				result: if L.result > 10.0 then 10.0 else L.result, // TODO: Units?
				distanceFromMidpoint: Abs(2.5 - L.result) // TODO: Units?
			}

define INRResultsPerDay =
	(
	    (distinct INRResultsByDay X return X.resultDate) D
		    return First(INRResultsByDay R where R.resultDate = D sort by R.distanceFromMidpoint)
    ) X
	sort by X.resultDate

define TherapeuticRange = interval[2.0, 3.0] // TODO: Units?

define INRIntervals =
	(INRResultsPerDay S return tuple { startResult: S, endResult: First(INRResultsPerDay E where S.resultDate > E.resultDate) }) X
		return
			tuple
			{
				startDate: X.startResult.resultDate,
				endDate: X.endResult.resultDate,
				resultDays: days between X.startResult.resultDate and X.endResult.resultDate,
				resultDifference: X.endResult.result - X.startResult.result,
				resultsWithinBounds: X.startResult.result in TherapeuticRange and X.endResult.result in TherapeuticRange,
				boundedDifference: 
					if X.endResult.result >= X.startResult.result
						then 
						(
							if x.startResult.result > end of TherapeuticRange or X.endResult.result < start of TherapeuticRange 
								then null 
								else Min(X.endResult.result, end of TherapeuticRange) - Max(X.startResult.result, start of TherapeuticRange)
						)
						else
						(
							if X.endResult.result > end of TherapeuticRange or X.startResult.result < start of TherapeuticRange
								then null
								else Min(X.startResult.result, end of TherapeuticRange) - Max(X.endResult.result, start of TherapeuticRange)
						),
				isValid: days between X.startResult.resultDate and X.endResult.resultDate <= 56
			}

define TherapeuticDays =
	INRIntervals I
		return
			tuple
			{
				startDate: I.startDate,
				endDate : I.endDate,
				isValid : I.isValid,
				resultdays : I.resultDays,
				daysInRange : 
					if I.resultsWithinBounds 
						then I.resultDays 
						else IfNull(I.resultDays * Abs(I.boundedDifference / (if I.resultDays = 0 then null else I.resultDays)), 0)
			}

define TherapeuticTimeInRange =
	Round(100 * (Sum(TherapeuticDays X return X.daysInRange) / Sum(TherapeuticDays X return X.resultDays)))

define NumberOfValidIntervals = Count(TherapeuticDays D where D.isValid)

define HasValidIntervals = NumberOfValidIntervals >= 2

define InitialPopulation =
	InDemographic
		and exists (InpatientEncounters)
		and exists (ActiveFlutterDiagnoses)
		and WarfarinUsage >= 180
		and not exists (ActiveValvularHeartDiseaseDiagnoses)

define MeasurePopulation =
	HasValidIntervals

define MeasureObservation =
	TherapeuticTimeInRange

context Population

define MeasureScore = Avg(MeasureObservation)


	