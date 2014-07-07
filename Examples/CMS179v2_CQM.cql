// Warfarin time in therapeutic range

using QUICK

context PATIENT

parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))

valueset "Flutter Diagnosis" = ValueSet('2.16.840.1.113883.3.117.1.7.1.202')
valueset "Warfarin Medication" = ValueSet('2.16.840.1.113883.3.117.1.7.1.232')
valueset "Face-to-face Encounter" = ValueSet('2.16.840.1.113883.3.464.1003.101.12.1048')
valueset "Office Visit" = ValueSet('2.16.840.1.113883.3.464.1003.101.12.1001')
valueset "Valvular Heart Disease" = ValueSet('2.16.840.1.113883.3.464.1003.104.12.1017')
valueset "INR Lab Result" = ValueSet('2.16.840.1.113883.3.117.1.7.1.213')

let FlutterDiagnoses = [Condition: "Flutter Diagnosis"]
let WarfarinMedications = [Medication, Administered: "Warfarin Medication"]
let FaceToFaceEncounters = [Encounter, Performance: "Face-to-face Encounter"]
let OfficeVisitEncounters = [Encounter, Performance: "Office Visit"]
let ValvularHeartDiseaseDiagnoses = [Condition: "Valvular Heart Disease"]
let INRLabResults = [ObservationResult: "INR Lab Result"]

let InDemographic =
	AgeAt(start of MeasurementPeriod) >= 18 years

let InpatientEncounters = union ( FaceToFaceEncounters, OfficeVisitEncounters )
let ActiveFlutterDiagnoses = FlutterDiagnoses F where F.effectiveTime overlaps before MeasurementPeriod
let ActiveValvularHeartDiseaseDiagnoses = ValvularHeartDiseaseDiagnoses D where D.effectiveTime overlaps before MeasurementPeriod

let LookbackInterval = interval[MeasurePeriodStart - 200 days, MeasurePeriodStart]

let ActiveWarfarinDuringLookback =
	WarfarinMedications M where M.administeredTime overlaps LookbackInterval

let WarfarinUsageIntervals =
	collapse 
		ActiveWarfarinDuringLookback M 
			return tuple { usageWithinLookback: M.administeredTime intersect LookbackInterval }

let WarfarinUsage = Sum(WarfarinUsageIntervals I return duration in days of I)

let INROutpatientLabResult = 
	INRLabResults R 
		where not exists (InpatientEncounters E where duration in hours of E.effectiveTime > 23 and R.observedAtTime starts during E.effectiveTime)

let INRResultsByDay =
	INROutpatientLabResult L 
		where L.result > 0.8 // TODO: Units?
		return 
			tuple 
			{ 
				resultDate: date of L.observedAtTime,
				result: if L.result > 10.0 then 10.0 else L.result, // TODO: Units?
				distanceFromMidpoint: Abs(2.5 - L.result) // TODO: Units?
			}

let INRResultsPerDay =
	(
	    (distinct INRResultsByDay X return X.resultDate) D
		    return First(INRResultsByDay R where R.resultDate = D sort by R.distanceFromMidpoint)
    ) X
	sort by X.resultDate

let TherapeuticRange = interval[2.0, 3.0] // TODO: Units?

let INRIntervals =
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

let TherapeuticDays =
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

let TherapeuticTimeInRange =
	Round(100 * (Sum(TherapeuticDays X return X.daysInRange) / Sum(TherapeuticDays X return X.resultDays)))

let NumberOfValidIntervals = Count(TherapeuticDays D where D.isValid)

let HasValidIntervals = NumberOfValidIntervals >= 2

let InitialPopulation =
	InDemographic
		and exists (InpatientEncounters)
		and exists (ActiveFlutterDiagnoses)
		and WarfarinUsage >= 180
		and not exists (ActiveValvularHeartDiseaseDiagnoses)

let MeasurePopulation =
	HasValidIntervals

let MeasureObservation =
	TherapeuticTimeInRange

context POPULATION

let MeasureScore = Avg(MeasureObservation)


	