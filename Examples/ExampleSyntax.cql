library ExampleSyntax

[UnknownOccurrence of Encounter, Performance]

[Encounter, Performance|Occurrence]

define AllProcedures = [Procedure, Performance]

define ImagingProcedures = AllProcedures P where P is [ImagingProcedure, Performance] return P as [ImagingProcedure, Performance]

A starts at most same day as start B

start of A <= start of B

A starts at least same day as start B

A starts 2 days before start B

start of A < (start of B - 2 days)

start of A < start of B and (duration in days between start of A and start of B) > 2

                A.datetime = 2014/01/15
                B.datetime = 2014/02

IntervalA: interval[Date(2014,1,15,0,0,0), Date(2014,1,15,23,59,59)]
IntervalB: interval[Date(2014,2,1,0,0,0), Date(2014,2,28,23,59,59)]

IntervalB: interval[Date(2014,1,30,0,0,0), Date(2014,2,26,23,59,59)]

end of IntervalA < start of IntervalB

start of A >= start of B

define MostRecentOfficeVisit =
	Last([Encounter, Performance: "Office Visit"] E sort by E.effectiveTime)

define HighBP =
	[SimpleObservationResult: "Systolic Blood Pressure"] SBP
		with (Last([Encounter, Performance: "Office Visit"] E sort by E.effectiveTime)) OV 
			where SBP.observedAtTime during OV.performanceTime
		where SBP.value < 140 u'mm[Hg]'

define HighBP =
	Last([Encounter, Performance: "Office Visit"] E sort by E.effectiveTime)


define InpatientCode = CodeableConcept { code: 'IMP', codeSystem: '2.16.840.1.113883.5.4' }

define InpatientEncounters = [Encounter, Performance] E where E.code = InpatientCode

define "Inpatient" = { CodeableConcept { code: 'IMP', codeSystem: '2.16.840.1.113883.5.4' } }

define InpatientEncounters = [Encounter, Performance: "Inpatient"]


// Declaration Examples

define "Severities" = ValueSet('http://hl7.fhir.org/vs/condition-severity')

{ Coding { code: 'fatal',  }, Coding { 'severe' }, Coding { 'moderate' }, Coding { 'mild' } }

define "Fatal Severities" = ValueSet('http://hl7.fhir.org/vs/fatal-condition-severities')

define "Fatal Severities" = "Severities" S where S.code = 'fatal'

[Condition: "Fatal Severities"]

InValueSet(Code { '90201' }, "Inpatient")

[Encounter, Performance] E where E.encounterType in "Inpatient"

[Condition: severity in "Severities"]

[Condition] C where C.severity.code = 'fatal'

[Condition: severity = 'fatal']


A = B
A > B
A >= B

A same year as B
A same minute as B // year, month, day, hour and minute are all the same

year of A = year of B
minute of A = minute of B // minute component A = minute component of B

Date(2014)
Date(2014, 7)
Date(2014, 7, 11)
Date(null, null, 11)

// does precision matter for < or >, yes but not the ability to specify what precision is used

Date(2014) = Date(2014) // true
Date(2014) = Date(2014, 6, 5) // unknown
Date(2014, 6, 5) = Date(2014, 6, 5) // true
Date(2014, 6, 6) = Date(2014, 6, 5) // false

A = B // Exact equality requires the same precision and the same values for all components

A < B
A > B // Comparison must always be performed based on the lowest common granularity

Date(2012) < Date(2014, 7, 11)
Date(2012, 1, 1, 0, 0, 0)..Date(2012, 12, 31, 23, 59, 59) < Date(2014, 7, 11, 0, 0, 0)..Date(2014, 7, 11, 23, 59, 59)

Date(2014) < Date(2014, 7, 11)

A <= B
A >= B // Combines exact equality with comparison semantics (i.e. A <= B <=> A = B or A < B)

start of A <= start of B using years

if (A is EncounterPerformanceOccurrence)

(A as EncounterPerformanceOccurrence).encounterType

if (A is [Encounter, Performance])

// Precision-based comparison
A same year as B

Date(2014) same day as Date(2014, 7, 11)

A at least B <=> A >= B
A at most 5 <=> A <= B

A same year as B
A at least same year as B // A < B or A same year as B???
A at most same year as B

A = B using years
A > B using years
A >= B using years

A starts before start B

A.start = Date(2012)
B.start = Date(2014, 2, 12)

// Method 1 - Intervals to minute precision
intervalA = interval[Date(2012, 1, 1, 0, 0, 0), Date(2013, 1, 1, 0, 0, 0))
intervalB = interval[Date(2014, 2, 12, 0, 0, 0), Date(2014, 2, 13, 0, 0, 0))

// Method 2 - Precision-based
year of A.start < year of B.start

A starts concurrent with start B

A same as B

// impact on intervals:
concurrent with
before/after
meets
meets before/after
overlaps
overlaps before/after
begins/begun by
ends/ended by
during/includes

// interval operations involving equality
concurrent with
meets before/after
overlaps before/after
begins/begun by
ends/ended by
during/includes

// impact on timing phrases
concurrent with
before/after
within..of
during
includes

// Only really need a solution for
concurrent with
during
includes

A starts during B using days
A concurrent with B using days

// Change concurrent with to same X as
// Works for intervals and timing phrases

// during
A during B, works fine assuming date/time comparison semantics because it involves inequality which will work

// meets doesn't work because there's equality involved, but I think that's the correct semantics for meets
// for example:
A = interval[Date(2012), Date(2013)]
B = interval[Date(2014, 1, 1), Date(2015, 1, 1))
A meets B is false



3 days = 72 hours

2.54 u"cm" = 1 u"in"

[Patient] P where P.height > 2.0 u"m"

where unit of P.height = u"m" and P.height > 2.0 u"m"

define IsTall = height.units = 'm' and height.value > 2

= <> < <= > >=
+ - * /


unit of X
value of X

no [Condition]
no [Encounter, Performance]

[Condition,Observation-UnknownOccurrence :ValueSet]
[Condition-Occurrence]
[Condition,Observation?]

[Encounter]

[Encounter,Performance]
[Encounter,Performance|Occurrence]
[Encounter,Performance|NonOccurrence]
[Encounter,Performance|UnknownOccurrence]

[TheTopic,TheModality|Uncertain]

[TheClass]

[Condition|Occurrence]
[Condition|NonOccurrence]
[Condition|UnknownOccurrence]



[no Encounter, Performance]

[<topic>, <modality>, <occurrence>]

[Condition, Nonoccurrence]
[Encounter, Performance, Nonoccurrence]

[non Condition]
[non Encounter, Performed]
[unknown Condition]
[unknown Encounter, Performed]



A starts

[Encounter, Performance] E
	with [Condition] C 
		where E.effectiveTime starts before or same day end C.effectiveTime

A starts during B

// Library definition
library CMS153 version '2'

// Data model reference
using QUICK

// Include Common library
include CMS153_Common version '2' as Common

// Define additional value sets for use within this artifact
valueset "Reason for not performing Chlamydia Screening" = ValueSet('TBD')

// Establish PATIENT context
context PATIENT

// Data Type examples
// Simple Types
define BooleanLiteral = true  false
define StringLiteral = 'female'  'pending', 'active', 'complete'
define ValuesetLiteral = "Female Administrative Sex"
define NumberLiteral = 16, -28,  100.015

// Clinical Types
define QuantityLiteral = 6 u'gm/cm3'  // UCUM units
80 u'mm[Hg]'
define TimespanLiteral = 3 months

// Structured Types
define Encounters = [Encounter, Performance]
define Info = tuple { Name: 'Patrick', DOB: Date(2014, 7, 5) }

define PharyngitisEffectiveTime = FirstPharyngitis.effectiveTime

no unknown

years months days hours minutes seconds milliseconds
date time year month day hour minute second millisecond timezone
date of X year of X minute of X


// List Types
define ListLiteral = { 1, 2, 3, 4, 5 }

// Interval Types
define IntervalLiteral = interval[Today() - 1 years, Today()]
interval[3, 5)

Today() - 1 years

months between X and Y

Count([Encounter, Performance])

Sum({ 1, 2, 3, 4, 5 })
Sum([ObservationResult] R return R.result)

if Count(X) > 0 then X[1] else 0

interval[3, 5) contains 4 
4 in interval[3, 5)

interval[3, null) contains 5

X starts before start Y = start of X < start of Y // fairly straightforward
X starts 3 days before start Y = start of X = start of Y - 3 days // ???
X starts at least 3 days before start Y = start of X <= start of Y - 3 days
X starts within 3 days before start Y = start of X >= start of Y - 3 days and start of X < start of Y // ???

<= 3 days starts before start of


X starts at least 3

5 minutes

// Simple Expressions
define LogicalAnd = A and B
define LogicalAndNotOr = A and not (B or C)
define GreaterOrEqual = A >= B
define NotEqual = A <> B
define Addition = A + B
define Precedence = A + B * C

5 = 'completed'
{ 1, 2, 3, 4, 5 } = { 1, 2, 3, 4, 5 }
{ 1, 2, 3, 4, 5 } <> { 5, 4, 3, 2, 1 }


// Timing/Interval Operations
define StartsBeforeStart = A starts before start B
define StartsConccurrent = A starts concurrent with B
define Starts3DaysBeforeStart = A starts 3 days before start B
define StartsWithinBefore = A starts within 3 days before start
define StartsWithinOf = A starts within 3 days of start

// Interval Operators
define Meets = A meets B
define Overlaps = A overlaps B
define During = A during B

// Point Access
define StartOf = start of MeasurementPeriod

// Membership
define Membership = X in interval[4, 6]

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

X starts within 3 days of start Y

dateTimeX within 3 days of dateTimeY

// Date/Time Manipulation
define DateSelector = Date(2014, 1, 1, 12, 0, 0, -6)
define Now = Now()
define Today = Today()
define ConvertedDate = convert '20140101120000-0600' to Datetime
define DateAdd = Today() + 3 months - 2 days
define DateDiff = months between start of X and end of X
define Duration = duration in months of X
define DateComponent = date of start of X
define TimeComponent = time of D
define Months = month of start of X

// Conditional Expressions
define IfExpression =
	// conditional expression
	if X > Y then X else Y

define SelectedCaseExpression =
	// selected case expression
	case X when 1 then 12 when 2 then 14 else 15 end

X is null X is not null
IfNull(X, Y) if X is null then Y else X
Substring("abcdefg", 1, 3)

Combine({ "ab", "cd", "ef" }) "abcdef"
Combine({ "completed", "refused", "pending" }, ";") "completed;refused;pending"
Split("completed;refused;pending", ";") { "completed", "refused", "pending" }


define GeneralCaseExpression =
	// general case expression
	case when X > Y then X when Y > X then Y else 0 end

// Aggregate Expressions
define CountExpression =
	Count([Encounter, Performance])

	Sum({ 1, 2, 3, 4, 5 })

	Avg([ObservationResult] R return R.result)

// Retrieve
define SimpleRetrieve =
	[Encounter, Performance]

define CodedRetrieve =
	[Encounter, Performance: "Ambulatory/ED Visit"]

define ExplicitlyCodedRetrieve =
	[Encounter, Performance: serviceType in "Ambulatory/ED Visit"]

define WhereExplicitlyCodedRetrieve =
	[Encounter, Performance where serviceType in "Ambulatory/ED Visit"]

define CodedWhere =
	[Encounter, Performance] E where E.serviceType in ValueSet("Ambulatory/ED Visit")

define RangedRetrieve =
	[Encounter, Performance, during MeasurementPeriod]

define ExplicitlyRangedRetrieve =
	[Encounter, Performance, performanceTime during MeasurementPeriod]

define WhereExplicitlyRangedRetrieve =
	[Encounter, Performance where performanceTime during MeasurementPeriod]

define RangedWhere =
	[Encounter, Performance] E where E.performanceTime during MeasurementPeriod

define CodedRangedRetrieve =
	[Encounter, Performance: "Ambulatory/ED Visit", during MeasurementPeriod]

define ExplicitlyCodedRangedRetrieve =
	[Encounter, Performance: serviceType in "Ambulatory/ED Visit", performanceTime during MeasurementPeriod]

define WhereExplicitlyCodedRangedRetrieve =
	[Encounter, Performance where serviceType in "Ambulatory/ED Visit" and performanceTime during MeasurementPeriod]

define CodedRangedWhere =
	[Encounter, Performance] E 
		where E.serviceType in ValueSet("Ambulatory/ED Visit") 
			and E.performanceTime during MeasurementPeriod

// Query
define Encounters =
	[Encounter, Performance: "Inpatient"] E

// Filter
define FilteredEncounters =
	[Encounter, Performance: "Inpatient"] E where duration in days of E.effectiveTime >= 120 days

// Return
define ProjectedEncounters =
	// Select columns to be returned
	[Encounter, Performance: "Inpatient"] E
		return tuple { id: E.id, effectiveTime: E.effectiveTime, serviceType: E.serviceType }

define ComputedEncounters =
	// Compute values for returned columns
	[Encounter, Performance: "Inpatient"] E 
		return tuple { id: E.id, lengthOfStay: duration in days of E.effectiveTime }

define ExtractedEncounters =
	// Extract values
	[Encounter, Performance: "Inpatient"] E
		return duration in days of E.effectiveTime

// Sort
define SortedEncounters =
	[Encounter, Performance: "Inpatient"] E 
		sort by effectiveTime

	[Encounter, Performance: "Inpatient"] E 
		return tuple { id: E.id, lengthOfStay: duration in days of E.effectiveTime } 
		sort by lengthOfStay desc

// With
define TargetEncounters =
	[Encounter, Performance: "Ambulatory/ED Visit"] E 
		with [Condition: "Acute Pharyngitis"] P where P.effectiveTime overlaps after E.PerformanceAtTime

define TargetEncounters =
	[Encounter, Performance: "Ambulatory/ED Visit"] E 
		with Pharyngitis P such that P.effectiveTime overlaps after E.performanceTime
		with Antibiotics A such that A.orderedAtTime starts within 3 days after start E.performanceTime 
	    where E.PerformanceAtTime during MeasurementPeriod

// Full Query
define FullQuery =
	[Encounter, Performance: "Inpatient"] E 
		with [Condition: "Acute Pharyngitis"] P 
			where P.effectiveTime overlaps after E.PerformanceAtTime 
		where duration in days of E.effectiveTime >= 120 days 
		return tuple { id: E.id, lengthOfStay: duration in days of E.EffectiveTime } 
		sort by lengthOfStay desc

// Combine
define EncountersAndConditions =
	[Encounter, Performance: "Ambulatory/ED Visit"] E
		combine [Condition: "Acute Pharyngitis"] P where P.effectiveTime overlaps after E.PerformanceAtTime

define FilteredEncountersAndConditions =
	EncountersAndConditions E where E.E.providerType in ValueSet("Provider Types")

define EncountersAndConditionsAndAntibiotics =
	FilteredEncountersAndConditions F combine [MedicationPrescription: "Antibiotic Medications"] A 
		where A.orderedAtTime within 3 days after start F.E.effectiveTime

define FilteredEncountersAndConditionsAndAntibiotics =
	EncountersWithConditionsAndAntibiotics E where E.A.quantity > 1

define EncountersAndConditions =
	[Encounter, Performance: "Ambulatory/ED Visit"] E
		combine [Condition: "Acute Pharyngitis"] P 
			where P.effectiveTime overlaps after E.PerformanceAtTime
		return tuple { Encounter: E, Pharyngitis: P }

define EncountersAndConditions =
	[Encounter, Performance: "Ambulatory/ED Visit"] E
		combine [Condition: "Acute Pharyngitis"] P 
			where P.effectiveTime overlaps after E.PerformanceAtTime
		return tuple { EncounterId: E.Id, EncounterEffectiveTime: E.effectiveTime, PharyngitisCode: P.Code }

ObservationResults R return R.result

define UnionedEncounters =
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

define X = tuple { X: 1, Y: 1 }
define NewX = X X return


// This combine
define EncountersAndConditions =
	[Encounter, Performance: "Ambulatory/ED Visit"] E
		combine [Condition: "Acute Pharyngitis"] P 
			where P.effectiveTime overlaps after E.PerformanceAtTime

// Is equivalent to nested foreach invocations
define CombinedEncountersAndConditions =
	expand
	(
		foreach E in [Encounter, Performance: "Ambulatory/ED Visit"]
			return
				foreach P in [Condition: "Acute Pharyngitis"]
					return tuple { Encounter: E, Condition: P }
	)

// followed by a filter
define EncountersAndConditions =
	CombinedEncountersAndConditions EC 
		where EC.Condition.effectiveTime overlaps after EC.Encounter.PerformanceAtTime


// Function definition
define function CumulativeDuration(Intervals: list<interval<Timestamp>>)
{
	return Sum((collapse Intervals) X return duration in days of X)
}

define Encounters = [Encounter, Performance: "Inpatient Visit"]

define CMD = CumulativeDuration(Encounters E return E.effectiveTime)

define CombinedValueSet = "Ambulatory/ED Visit" union "Inpatient Visit"

define XYZCode = First(CombinedValueSet VS where VS.code = 'XYZ')
define XYZCode = tuple { code: 'XYZ' }

Sum({ 1, 2, 3, 4, 5 })
Count([Encounter, Performance])

Sum(result from [ObservationResult])


Sum(foreach R in [ObservationResult] return R.result)
Min(foreach E in [Encounter, Performance] return R.effectiveTime)


foreach R in [ObservationResult] return R.result


// foreach vs return clause

define Z = foreach X in [Encounter, Performance]
	return foreach Y in [ObservationResult] return tuple { X: X, Y: Y }

define A = [Encounter, Performance] X return [ObservationResult] Y return tuple { X: X, Y: Y }

// Aggregates

define A = Sum(result from [ObservationResult])
define A = Sum([ObservationResult] X return X.result)
define A = Sum({ 1, 2, 3, 4, 5 })

define Encounters =
	[Encounter, Performance, during MeasurementPeriod]

define EncountersWithDates =
	Encounters X return tuple { encounter: X, effectiveDate: date of X.effectiveTime }

define Dates =
	distinct (EncountersWithDates X return X.effectiveDate)

define NumberOfEncountersPerDate =
	Dates D return tuple { encounterDate: D, number: Count(EncountersWithDates E where E.effectiveDate = D) }

using QUICK

context = PATIENT

define InCohort = year of birthDate >= 1965 and year of birthDate < 1992

define Transfusions = [Procedure, Performance: "Blood transfusion"] P where year of P.performanceTime < 1992



// Slide Examples:

// Simple Example:

library CMS153

using QUICK

parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))

valueset "Female Administrative Sex" = ValueSet('2.16.840.1.113883.3.560.100.2')

context PATIENT

define InDemographic =
	AgeAt(start of MeasurementPeriod) >= 16
		and AgeAt(start of MeasurementPeriod) < 24
		and Gender in "Female Administrative Sex"

// Equivalent QDM
AND: Age >= 16 year(s) at: "Measurement Period"
AND: Age < 24 year(s) at: "Measurement Period"
AND: "Patient Characteristic Sex: Female"

// Population Criteria

// NOTE: Other clinically relevant indicators omitted for brevity
define SexuallyActive =
	exists ([Condition: "Chlamydia"] C where C.effectiveTime overlaps MeasurementPeriod)
	or exists ([Condition: "HIV"] C where C.effectiveTime overlaps MeasurementPeriod)
	or exists ([Condition: "Syphilis"] C where C.effectiveTime overlaps MeasurementPeriod)

// Equivalent QDM
OR: "Diagnosis, Active: Chlamydia" overlaps "Measurement Period"
OR: "Diagnosis, Active: HIV" overlaps "Measurement Period"
OR: "Diagnosis, Active: Syphilis" overlaps "Measurement Period"

// Timing Phrases

// Assumes previous definition of Antibiotics and TargetDiagnoses
define HasPriorAntibiotics =
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

library CMS153_CQM

using QUICK

parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))

valueset "Female Administrative Sex" = ValueSet('2.16.840.1.113883.3.560.100.2')
... // Additional valueset definitions omitted for brevity

context PATIENT

define InDemographic =
	AgeAt(start of MeasurementPeriod) >= 16
		and AgeAt(start of MeasurementPeriod) < 24
		and Gender in "Female Administrative Sex"

define SexuallyActive =
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

define InInitialPopulation =
	InDemographic and SexuallyActive

define InDenominator =
	InInitialPopulation

define InNumerator =
	InDenominator
		and exists ([ObservationResult: "Chlamydia Screening"] R where R.effectiveTime during MeasurementPeriod and R.result is not null)

// Chlamydia Screening, CDS

library CMS153_CDS

using QUICK

valueset "Female Administrative Sex" = ValueSet('2.16.840.1.113883.3.560.100.2')
... // Other valueset definitions omitted for brevity

context PATIENT

define InDemographic =
	Age() >= 16 and Age() < 24 and Gender in "Female Administrative Sex"

define SexuallyActive =
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
		
define NoScreening =
	not exists ([ObservationResult: "Chlamydia Screening", during interval[Today() - 1 years, Today()]] R where R.result is not null)
	and not exists ([Procedure, Plan: "Chlamydia Screening", during interval[Today(), null]])
	and not exists ([ObservationResult: "Reason for not performing Chlamydia Screening"])

define NeedScreening = InDemographic and SexuallyActive and NoScreening


// Chlamydia Screening, Common

library CMS153_Common

using QUICK

valueset "Female Administrative Sex" = ValueSet('2.16.840.1.113883.3.560.100.2')
... // Other valueset definitions omitted for brevity

context PATIENT

define ConditionsIndicatingSexualActivity =
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

define ResultsIndicatingSexualActivity =
    union(
        [ObservationResult: "Pregnancy"],
        [ObservationResult: "Pap"],
        [ObservationResult: "Lab Tests During Pregnancy"],
        [ObservationResult: "Lab Tests for Sexually Transmitted Infections"]
    )

define ResultsPresentForChlamydiaScreening =
    [ObservationResult: "Chlamydia Screening"] R where R.result is not null

not exists ([Condition])

X in "Acute Pharyngitis"

// Chlamydia Screening, CQM, Using Common

library CMS153_CQM version '2'

using QUICK

include CMS153_Common version '2' as Common

parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))

parameter MeasurementPeriod : interval<Datetime>

context PATIENT

define InDemographic =
    AgeAt(start of MeasurementPeriod) >= 16
        and AgeAt(start of MeasurementPeriod) < 24
        and Gender in Common."Female Administrative Sex"

define SexuallyActive =
    exists(Common.ConditionsIndicatingSexualActivity C where C.effectiveTime overlaps before MeasurementPeriod)
        or exists(Common.ResultsIndicatingSexualActivity R where R.effectiveTime during MeasurementPeriod)

define InInitialPopulation =
    InDemographic and SexuallyActive

define InDenominator =
    InInitialPopulation

define InNumerator =
    InDenominator 
		and exists (Common.ResultsPresentForChlamydiaScreening S where S.effectiveTime during MeasurementPeriod)


// Chlamydia Screening, CDS, Using Common

library CMS153_CDS

using QUICK

include CMS153_Common as Common

valueset "Reason for not performing Chlamydia Screening" = ValueSet('TBD')

context PATIENT

define InDemographic =
    Age() >= 16 and Age() < 24 and Gender in Common."Female Administrative Sex"

define SexuallyActive =
    exists (Common.ConditionsIndicatingSexualActivity)
    or exists (Common.ResultsIndicatingSexualActivity)

define NoScreening =
    not exists (Common.ResultsPresentForChlamydiaScreening S where S.effectiveTime during interval[Today() - 1 years, Today()])
    and not exists ([Procedure, Plan: Common."Chlamydia Screening"] P where P.effectiveTime >= now)
    and not exists ([ObservationResult: "Reason for not performing Chlamydia Screening"])

define NeedScreening = InDemographic and SexuallyActive and NoScreening

define TargetEncounters =
	[Encounter, Performance: "Ambulatory/ED Visit"] E 
		with [Condition: "Acute Pharyngitis"] P 
			where not P.effectiveTime overlaps after E.performanceTime

define TargetEncounters =
	[Encounter, Performance: "Ambulatory/ED Visit"] E 
		where exists ([Condition: "Acute Pharyngitis"] P where P.effectiveTime overlaps after E.performanceTime)

define TargetEncounters =
	[Encounter, Performance: "Ambulatory/ED Visit"] E 
		without [Condition: "Acute Pharyngitis"] P where P.effectiveTime overlaps after E.performanceTime

define TargetEncounters =
	[Encounter, Performance: "Ambulatory/ED Visit"] E 
		where not exists ([Condition: "Acute Pharyngitis"] P where P.effectiveTime overlaps after E.performanceTime)

