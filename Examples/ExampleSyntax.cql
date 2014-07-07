library ExampleSyntax

// Declaration Examples

// Data model reference
using QUICK

// Establish PATIENT context
context PATIENT

// Include Common library
include 'ChlamydiaScreening_Common.cql' as Common

// Define additional value sets for use within this artifact
valueset "Reason for not performing Chlamydia Screening" = ValueSet('TBD')

// Data Type examples
// Simple Types
let BooleanLiteral = true
let StringLiteral = 'female'
let ValuesetLiteral = "Female Administrative Sex"
let NumberLiteral = 16

// Clinical Types
let QuantityLiteral = 6 u"gm/cm3"  // UCUM units
let TimespanLiteral = 3 months

// Structured Types
let Encounters = [Encounter, Performance]
let TupleLiteral = tuple { Name: 'Patrick', DOB: Date(2014, 1, 1) }

// List Types
let ListLiteral = { 1, 2, 3, 4, 5 }

// Interval Types
let IntervalLiteral = interval[today - 1 years, today]

// Simple Expressions
let LogicalAnd = A and B
let LogicalAndNotOr = A and not (B or C)
let GreaterOrEqual = A >= B
let NotEqual = A <> B
let Addition = A + B
let Precedence = A + B * C

// Timing/Interval Operations
let StartsBeforeStart = A starts before start B
let StartsConccurrent = A starts concurrent with B
let Starts3DaysBeforeStart = A starts 3 days before start B
let StartsWithinBefore = A starts within 3 days before start
let StartsWithinOf = A starts within 3 days of start

// Interval Operators
let Meets = A meets B
let Overlaps = A overlaps B
let During = A during B

// Point Access
let StartOf = start of MeasurementPeriod

// Membership
let Membership = X in interval[4, 6]

// List Operations

// Selector
{ 1, 2, 3, 4, 5 }

// Membership
X in { 1, 2, 3, 4, 5 }
{ 1, 2, 3, 4, 5 } contains X

// Comparison
L = { 4, 5, 6 } // true if L has the same elements
L includes { 4, 5, 6 } // true if L includes each element
{ 4, 5, 6 } included in L // equivalent to above

// Indexer
{ 4, 5, 6 }[1] // 1-based, evaluates to 4

// Count
Count({ 4, 5, 6 }) // evaluates to 3

// First/Last
First({ 4, 5, 6 }) // evaluates to 4

// Date/Time Manipulation
let DateSelector = Date(2014, 1, 1, 12, 0, 0, -6)
let Now = now
let Today = today
let ConvertedDate = convert '20140101120000-0600' to Timestamp
let DateAdd = today + 3 months - 2 days
let DateDiff = months between start of X and end of X
let Duration = duration in months of X
let DateComponent = date of start of X
let TimeComponent = time of D
let Months = month of start of X

// Conditional Expressions
let IfExpression = 
	// conditional expression
	if X > Y then X else Y

let SelectedCaseExpression = 
	// selected case expression
	case X when 1 then 12 when 2 then 14 else 15 end

let GeneralCaseExpression = 
	// general case expression
	case when X > Y then X when Y > X then Y else 0 end

// Aggregate Expressions
let CountExpression =
	Count([Encounter, Performance])

	Sum({ 1, 2, 3, 4, 5 })

	Avg([ObservationResult] R return R.result)

// Retrieve
let SimpleRetrieve =
	[Encounter, Performance]

let CodedRetrieve =
	[Encounter, Performance: "Ambulatory/ED Visit"]

let ExplicitlyCodedRetrieve =
	[Encounter, Performance: serviceType in "Ambulatory/ED Visit"]

let WhereExplicitlyCodedRetrieve =
	[Encounter, Performance where serviceType in "Ambulatory/ED Visit"]

let CodedWhere =
	[Encounter, Performance] E where E.serviceType in ValueSet("Ambulatory/ED Visit")

let RangedRetrieve =
	[Encounter, Performance, during MeasurementPeriod]

let ExplicitlyRangedRetrieve =
	[Encounter, Performance, PerformanceAtTime during MeasurementPeriod]

let WhereExplicitlyRangedRetrieve =
	[Encounter, Performance where PerformanceAtTime during MeasurementPeriod]

let RangedWhere =
	[Encounter, Performance] E where E.PerformanceAtTime during MeasurementPeriod

let CodedRangedRetrieve =
	[Encounter, Performance: "Ambulatory/ED Visit", during MeasurementPeriod]

let ExplicitlyCodedRangedRetrieve =
	[Encounter, Performance: serviceType in "Ambulatory/ED Visit", PerformanceAtTime during MeasurementPeriod]

let WhereExplicitlyCodedRangedRetrieve =
	[Encounter, Performance where serviceType in "Ambulatory/ED Visit" and PerformanceAtTime during MeasurementPeriod]

let CodedRangedWhere =
	[Encounter, Performance] E 
		where E.serviceType in ValueSet("Ambulatory/ED Visit") 
			and E.PerformanceAtTime during MeasurementPeriod

// Query
let Encounters =
	[Encounter, Performance: "Inpatient"] E

// Filter
let FilteredEncounters =
	[Encounter, Performance: "Inpatient"] E
		where duration in days of E.effectiveTime >= 120 days

// Return
let ProjectedEncounters =
	// Select columns to be returned
	[Encounter, Performance: "Inpatient"] E
		return tuple { id: E.id, effectiveTime: E.effectiveTime, serviceType: E.serviceType }

let ComputedEncounters =
	// Compute values for returned columns
	[Encounter, Performance: "Inpatient"] E
		return tuple { id: E.id, lengthOfStay: duration in days of E.effectiveTime }

let ExtractedEncounters =
	// Extract values
	[Encounter, Performance: "Inpatient"] E
		return duration in days of E.effectiveTime

// Sort
let SortedEncounters =
	[Encounter, Performance: "Inpatient"] E
		sort by effectiveTime

	[Encounter, Performance: "Inpatient"] E
		return tuple { id: E.id, lengthOfStay: duration in days of E.effectiveTime }
		sort by lengthOfStay desc

// With
let TargetEncounters =
	[Encounter, Performance: "Ambulatory/ED Visit"] E
		with [Condition: "Acute Pharyngitis"] P
			where P.effectiveTime overlaps after E.PerformanceAtTime

let TargetEncounters =
	[Encounter, Performance: "Ambulatory/ED Visit"] E 
		with Pharyngitis P where P.effectiveTime overlaps after E.PerformanceAtTime
		with Antibiotics A where A.orderedAtTime starts within 3 days after start E.PerformanceAtTime 
	    where E.PerformanceAtTime during MeasurementPeriod

// Full Query
let FullQuery =
	[Encounter, Performance: "Inpatient"] E
		with [Condition: "Acute Pharyngitis"] P where P.effectiveTime overlaps after E.PerformanceAtTime
		where duration in days of E.effectiveTime >= 120 days
		return tuple { id: E.id, lengthOfStay: duration in days of E.EffectiveTime }
		sort by lengthOfStay desc

// Combine
let EncountersAndConditions =
	[Encounter, Performance: "Ambulatory/ED Visit"] E
		combine [Condition: "Acute Pharyngitis"] P where P.effectiveTime overlaps after E.PerformanceAtTime

let FilteredEncountersAndConditions =
	EncountersAndConditions E where E.E.providerType in ValueSet("Provider Types")

let EncountersAndConditionsAndAntibiotics =
	FilteredEncountersAndConditions F combine [MedicationPrescription: "Antibiotic Medications"] A 
		where A.orderedAtTime within 3 days after start F.E.effectiveTime

let FilteredEncountersAndConditionsAndAntibiotics =
	EncountersWithConditionsAndAntibiotics E where E.A.quantity > 1

let EncountersAndConditions =
	[Encounter, Performance: "Ambulatory/ED Visit"] E
		combine [Condition: "Acute Pharyngitis"] P 
			where P.effectiveTime overlaps after E.PerformanceAtTime
		return tuple { Encounter: E, Pharyngitis: P }

let EncountersAndConditions =
	[Encounter, Performance: "Ambulatory/ED Visit"] E
		combine [Condition: "Acute Pharyngitis"] P 
			where P.effectiveTime overlaps after E.PerformanceAtTime
		return tuple { EncounterId: E.Id, EncounterEffectiveTime: E.effectiveTime, PharyngitisCode: P.Code }

ObservationResults R return R.result

let UnionedEncounters =
	[Encounter, Performance: "Ambulatory/ED Visit"] 
		union [Encounter, Performance: "Inpatient"]

	[Encounter, Performance: "Ambulatory/ED Visit"]
		intersect [Encounter, Performance: "Inpatient"]

	[Encounter, Performance: "Ambulatory/ED Visit"]
		except [Encounter, Performance: "Inpatient"]

// retrieve
// filter
// foreach
// sort
// with/combine
// redefine

let X = tuple { X: 1, Y: 1 }
let NewX = X X return 


// This combine
let EncountersAndConditions =
	[Encounter, Performance: "Ambulatory/ED Visit"] E
		combine [Condition: "Acute Pharyngitis"] P 
			where P.effectiveTime overlaps after E.PerformanceAtTime

// Is equivalent to nested foreach invocations
let CombinedEncountersAndConditions =
	expand
	(
		foreach E in [Encounter, Performance: "Ambulatory/ED Visit"]
			return
				foreach P in [Condition: "Acute Pharyngitis"]
					return tuple { Encounter: E, Condition: P }
	)

// followed by a filter
let EncountersAndConditions =
	CombinedEncountersAndConditions EC 
		where EC.Condition.effectiveTime overlaps after EC.Encounter.PerformanceAtTime


// Function definition
define function CumulativeDuration(Intervals: list<interval<Timestamp>>)
{
	return Sum((collapse Intervals) X return duration in days of X)
}

let Encounters = [Encounter, Performance: "Inpatient Visit"]

let CMD = CumulativeDuration(Encounters E return E.effectiveTime)

let CombinedValueSet = "Ambulatory/ED Visit" union "Inpatient Visit"

let XYZCode = First(CombinedValueSet VS where VS.code = 'XYZ')
let XYZCode = tuple { code: 'XYZ' }

Sum({ 1, 2, 3, 4, 5 })
Count([Encounter, Performance])

Sum(result from [ObservationResult])


Sum(foreach R in [ObservationResult] return R.result)
Min(foreach E in [Encounter, Performance] return R.effectiveTime)


foreach R in [ObservationResult] return R.result


// foreach vs return clause

let Z = foreach X in [Encounter, Performance]
	return foreach Y in [ObservationResult] return tuple { X: X, Y: Y }

let A = [Encounter, Performance] X return [ObservationResult] Y return tuple { X: X, Y: Y }

// Aggregates

let A = Sum(result from [ObservationResult])
let A = Sum([ObservationResult] X return X.result)
let A = Sum({ 1, 2, 3, 4, 5 })

let Encounters = 
	[Encounter, Performance, during MeasurementPeriod]

let EncountersWithDates = 
	Encounters X return tuple { encounter: X, effectiveDate: date of X.effectiveTime }

let Dates = 
	distinct (EncountersWithDates X return X.effectiveDate)

let NumberOfEncountersPerDate = 
	Dates D return tuple { encounterDate: D, number: Count(EncountersWithDates E where E.effectiveDate = D) }

using QUICK

context = PATIENT

let InCohort = year of birthDate >= 1965 and year of birthDate < 1992

let Transfusions = [Procedure, Performance: "Blood transfusion"] P where year of P.performanceTime < 1992



// Slide Examples:

// Simple Example:

using QUICK

context PATIENT

parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))

valueset "Female Administrative Sex" = ValueSet('2.16.840.1.113883.3.560.100.2')

let InDemographic = 
	AgeAt(start of MeasurementPeriod) >= 16
		and AgeAt(start of MeasurementPeriod) < 24
		and Gender in "Female Administrative Sex"

// Equivalent QDM
AND: Age >= 16 year(s) at: "Measurement Period"
AND: Age < 24 year(s) at: "Measurement Period"
AND: "Patient Characteristic Sex: Female"

// Population Criteria

// NOTE: Other clinically relevant indicators omitted for brevity
let SexuallyActive =
	exists ([Condition: "Chlamydia"] C where C.effectiveTime overlaps MeasurementPeriod)
	or exists ([Condition: "HIV"] C where C.effectiveTime overlaps MeasurementPeriod)
	or exists ([Condition: "Syphilis"] C where C.effectiveTime overlaps MeasurementPeriod)

// Equivalent QDM
OR: "Diagnosis, Active: Chlamydia" overlaps "Measurement Period"
OR: "Diagnosis, Active: HIV" overlaps "Measurement Period"
OR: "Diagnosis, Active: Syphilis" overlaps "Measurement Period"

// Timing Phrases

// Assumes previous definition of Antibiotics and TargetDiagnoses
let HasPriorAntibiotics =
	exists (
		Antibiotics A
			with TargetDiagnoses D 
				where A.orderedAtTime starts within 30 days before start D.effectiveTime
	)

// Equivalent QDM
AND: "Medication, Order: Antibiotic Medications" <= 30 days(s) starts before start of
	OR: "Diagnosis, Active: Acute Pharyngitis"
	OR: "Diagnosis, Active: Acute Tonsillitis"

// Chlamydia Screening, CQM

using QUICK

context PATIENT

parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))

valueset "Female Administrative Sex" = ValueSet('2.16.840.1.113883.3.560.100.2')
... // Additional valueset definitions omitted for brevity

let InDemographic = 
	AgeAt(start of MeasurementPeriod) >= 16
		and AgeAt(start of MeasurementPeriod) < 24
		and Gender in "Female Administrative Sex"

let SexuallyActive =
	exists ([Condition: "Other Female Reproductive Conditions"] C where C.effectiveTime overlaps before MeasurementPeriod)
		or exists ([Condition: "Genital Herpes"] C where C.effectiveTime overlaps before MeasurementPeriod)
		or exists ([Condition: "Genococcal Infections and Venereal Diseases"] C where C.effectiveTime overlaps before MeasurementPeriod)
		or exists ([Condition: "Inflammatory Diseases of Female Reproductive Organs"] C where C.effectiveTime overlaps before MeasurementPeriod)
		or exists ([Condition: "Chlamydia"] C where C.effectiveTime overlaps before MeasurementPeriod)
		or exists ([Condition: "HIV"] C where C.effectiveTime overlaps before MeasurementPeriod)
		or exists ([Condition: "Syphilis"] C where C.effectiveTime overlaps before MeasurementPeriod)
		or exists ([Condition: "Complications of Pregnancy, Childbirth and the Puerperium"] C where C.effectiveTime overlaps before MeasurementPeriod)
		or exists ([ObservationResult: "Pregnancy"] R where R.effectiveTime during MeasurementPeriod)
		or exists ([ObservationResult: "Pap"] R where R.effectiveTime during MeasurementPeriod)
		or exists ([ObservationResult: "Lab Tests During Pregnancy"] R where R.effectiveTime during MeasurementPeriod)
		or exists ([ObservationResult: "Lab Tests for Sexually Transmitted Infections"] R where R.observedAtTime during MeasurementPeriod)

let InInitialPopulation =
	InDemographic and SexuallyActive

let InDenominator =
	InInitialPopulation

let InNumerator =
	InDenominator
		and exists ([ObservationResult: "Chlamydia Screening"] R where R.effectiveTime during MeasurementPeriod and R.result is not null)

// Chlamydia Screening, CDS

using QUICK

context PATIENT

valueset "Female Administrative Sex" = ValueSet('2.16.840.1.113883.3.560.100.2')
... // Other valueset definitions omitted for brevity

let InDemographic = 
	Age() >= 16 and Age() < 24 and Gender in "Female Administrative Sex"

let SexuallyActive =
	exists ([Condition: "Other Female Reproductive Conditions"])
	or exists ([Condition: "Genital Herpes"])
	or exists ([Condition: "Genococcal Infections and Venereal Diseases"])
	or exists ([Condition: "Inflammatory Diseases of Female Reproductive Organs"])
	or exists ([Condition: "Chlamydia"])
	or exists ([Condition: "HIV"])
	or exists ([Condition: "Syphilis"])
	or exists ([Condition: "Complications of Pregnancy, Childbirth and the Puerperium"])
	or exists ([ObservationResult: "Pregnancy Test"])
	or exists ([ObservationResult: "Pap Test"])
	or exists ([ObservationResult: "Lab Tests During Pregnancy"])
	or exists ([ObservationResult: "Lab Tests for Sexually Transmitted Infections"])
		
let NoScreening =
	not exists ([ObservationResult: "Chlamydia Screening", during interval[today - 1 years, today]] R where R.result is not null)
	and not exists ([Procedure, Plan: "Chlamydia Screening", during interval[today, null]])
	and not exists ([ObservationResult: "Reason for not performing Chlamydia Screening"])

let NeedScreening = InDemographic and SexuallyActive and NoScreening


// Chlamydia Screening, Common

using QUICK

context PATIENT

valueset "Female Administrative Sex" = ValueSet('2.16.840.1.113883.3.560.100.2')
... // Other valueset definitions omitted for brevity

let ConditionsIndicatingSexualActivity =
    union(
        [Condition: "Other Female Reproductive Conditions"],
        [Condition: "Genital Herpes"],
        [Condition: "Genococcal Infections and Venereal Diseases"],
        [Condition: "Inflammatory Diseases of Female Reproductive Organs"],
        [Condition: "Chlamydia"],
        [Condition: "HIV"],
        [Condition: "Syphilis"],
        [Condition: "Complications of Pregnancy, Childbirth and the Puerperium"]
    )

let ResultsIndicatingSexualActivity =
    union(
        [ObservationResult: "Pregnancy"],
        [ObservationResult: "Pap"],
        [ObservationResult: "Lab Tests During Pregnancy"],
        [ObservationResult: "Lab Tests for Sexually Transmitted Infections"]
    )

let ResultsPresentForChlamydiaScreening = 
    [ObservationResult: "Chlamydia Screening"] R where R.result is not null

// Chlamydia Screening, CQM, Using Common

using QUICK

context PATIENT

include 'ChlamydiaScreening_Common.cql' version '1' as Common

parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))

let InDemographic =
    AgeAt(start of MeasurementPeriod) >= 16
        and AgeAt(start of MeasurementPeriod) < 24
        and Gender in Common."Female Administrative Sex"

let SexuallyActive =
    exists(Common.ConditionsIndicatingSexualActivity C where C.effectiveTime overlaps before MeasurementPeriod)
        or exists(Common.ResultsIndicatingSexualActivity R where R.effectiveTime during MeasurementPeriod)

let InInitialPopulation =
    InDemographic and SexuallyActive

let InDenominator =
    InInitialPopulation

let InNumerator =
    InDenominator 
		and exists (Common.ResultsPresentForChlamydiaScreening S where S.effectiveTime during MeasurementPeriod)


// Chlamydia Screening, CDS, Using Common

using QUICK

context PATIENT

include 'ChlamydiaScreening_Common.cql' as Common

valueset "Reason for not performing Chlamydia Screening" = ValueSet('TBD')

let InDemographic =
    Age() >= 16 and Age() < 24 and Gender in Common."Female Administrative Sex"

let SexuallyActive =
    exists (Common.ConditionsIndicatingSexualActivity)
    or exists (Common.ResultsIndicatingSexualActivity)

let NoScreening =
    not exists (Common.ResultsPresentForChlamydiaScreening S where S.effectiveTime during interval[today - 1 years, today])
    and not exists ([Procedure, Plan: Common."Chlamydia Screening"] P where P.effectiveTime >= now)
    and not exists ([ObservationResult: "Reason for not performing Chlamydia Screening"])

let NeedScreening = InDemographic and SexuallyActive and NoScreening

