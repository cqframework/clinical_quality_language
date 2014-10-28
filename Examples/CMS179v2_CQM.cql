library CMS179 version '2'

// Warfarin time in therapeutic range

using QUICK

parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))

define "Flutter Diagnosis" = ValueSet('2.16.840.1.113883.3.117.1.7.1.202')
define "Warfarin Medication" = ValueSet('2.16.840.1.113883.3.117.1.7.1.232')
define "Face-to-face Encounter" = ValueSet('2.16.840.1.113883.3.464.1003.101.12.1048')
define "Office Visit" = ValueSet('2.16.840.1.113883.3.464.1003.101.12.1001')
define "Valvular Heart Disease" = ValueSet('2.16.840.1.113883.3.464.1003.104.12.1017')
define "INR Lab Result" = ValueSet('2.16.840.1.113883.3.117.1.7.1.213')

context PATIENT

define FlutterDiagnoses = [Condition: "Flutter Diagnosis"]
define WarfarinMedications = [Medication, Administered: "Warfarin Medication"]
define FaceToFaceEncounters = [Encounter, Performance: "Face-to-face Encounter"]
define OfficeVisitEncounters = [Encounter, Performance: "Office Visit"]
define ValvularHeartDiseaseDiagnoses = [Condition: "Valvular Heart Disease"]
define INRLabResults = [ObservationResult: "INR Lab Result"]

define InDemographic =
	AgeAt(start of MeasurementPeriod) >= 18 years

define InpatientEncounters = FaceToFaceEncounters union OfficeVisitEncounters
define ActiveFlutterDiagnoses = FlutterDiagnoses F where F.effectiveTime overlaps before MeasurementPeriod
define ActiveValvularHeartDiseaseDiagnoses = ValvularHeartDiseaseDiagnoses D where D.effectiveTime overlaps before MeasurementPeriod

define LookbackInterval = interval[MeasurePeriodStart - 200 days, MeasurePeriodStart]

define ActiveWarfarinDuringLookback =
	WarfarinMedications M where M.administeredTime overlaps LookbackInterval

define WarfarinUsageIntervals =
	collapse 
		ActiveWarfarinDuringLookback M 
			return tuple { usageWithinLookback: M.administeredTime intersect LookbackInterval }

define WarfarinUsage = Sum(WarfarinUsageIntervals I return duration in days of I)

define INROutpatientLabResult =
	INRLabResults R 
		where not exists (InpatientEncounters E where duration in hours of E.effectiveTime > 23 and R.observedAtTime starts during E.effectiveTime)

define INRResultsByDay =
	INROutpatientLabResult L 
		where L.result > 0.8 // TODO: Units?
		return 
			tuple 
			{ 
				resultDate: date of L.observedAtTime,
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

context POPULATION

define MeasureScore = Avg(MeasureObservation)


	