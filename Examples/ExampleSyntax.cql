library ExampleSyntax

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
let Encounters = [Encounter, Performed]
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
let Membership = X in { 1, 2, 3, 4, 5 }

// Date/Time Manipulation
let DateSelector = Date(2014, 1, 1, 12, 0, 0, -6)
let Now = now
let Today = today
let ConvertedDate = convert '20140101120000-0600' to Timestamp
let DateAdd = today + 3 months - 2 days
let DateDiff = months between start of X and end of X
let Duration = duration in months of X
let DateComponent = date of start of X
let Months = month of start of X

// Retrieve
let SimpleRetrieve =
	[Encounter, Performed]

let CodedRetrieve =
	[Encounter, Performed: "Ambulatory/ED Visit"]

let ExplicitlyCodedRetrieve =
	[Encounter, Performed: serviceType in "Ambulatory/ED Visit"]

let WhereExplicitlyCodedRetrieve =
	[Encounter, Performed where serviceType in "Ambulatory/ED Visit"]

let CodedWhere =
	[Encounter, Performed] E where E.serviceType in ValueSet("Ambulatory/ED Visit")

let RangedRetrieve =
	[Encounter, Performed, during MeasurementPeriod]

let ExplicitlyRangedRetrieve =
	[Encounter, Performed, performedAtTime during MeasurementPeriod]

let WhereExplicitlyRangedRetrieve =
	[Encounter, Performed where performedAtTime during MeasurementPeriod]

let RangedWhere =
	[Encounter, Performed] E where E.performedAtTime during MeasurementPeriod

let CodedRangedRetrieve =
	[Encounter, Performed: "Ambulatory/ED Visit", during MeasurementPeriod]

let ExplicitlyCodedRangedRetrieve =
	[Encounter, Performed: serviceType in "Ambulatory/ED Visit", performedAtTime during MeasurementPeriod]

let WhereExplicitlyCodedRangedRetrieve =
	[Encounter, Performed where serviceType in "Ambulatory/ED Visit" and performedAtTime during MeasurementPeriod]

let CodedRangedWhere =
	[Encounter, Performed] E 
		where E.serviceType in ValueSet("Ambulatory/ED Visit") 
			and E.performedAtTime during MeasurementPeriod

// With
let TargetEncounters =
	[Encounter, Performed: "Ambulatory/ED Visit"] E
		with [Condition: "Acute Pharyngitis"] P
			where P.effectiveTime overlaps after E.performedAtTime

let TargetEncounters =
	[Encounter, Performed: "Ambulatory/ED Visit"] E 
		with Pharyngitis P where P.effectiveTime overlaps after E.performedAtTime
		with Antibiotics A where A.orderedAtTime starts within 3 days after start E.performedAtTime 
	    where E.performedAtTime during MeasurementPeriod

let EncountersAndConditions =
	[Encounter, Performed: "Ambulatory/ED Visit"] E
		combine [Condition: "Acute Pharyngitis"] P 
			where P.effectiveTime overlaps after E.performedAtTime

let FilteredEncountersAndConditions =
	EncountersAndConditions E where E.E.providerType in ValueSet("Provider Types")

let EncountersAndConditionsAndAntibiotics =
	FilteredEncountersAndConditions F combine [MedicationPrescription: "Antibiotic Medications"] A 
		where A.orderedAtTime within 3 days after start F.E.effectiveTime

let FilteredEncountersAndConditionsAndAntibiotics =
	EncountersWithConditionsAndAntibiotics E where E.A.quantity > 1

let EncountersAndConditions =
	[Encounter, Performed: "Ambulatory/ED Visit"] E
		combine [Condition: "Acute Pharyngitis"] P 
			where P.effectiveTime overlaps after E.performedAtTime
		return tuple { Encounter: E, Pharyngitis: P }

let EncountersAndConditions =
	[Encounter, Performed: "Ambulatory/ED Visit"] E
		combine [Condition: "Acute Pharyngitis"] P 
			where P.effectiveTime overlaps after E.performedAtTime
		return tuple { EncounterId: E.Id, EncounterEffectiveTime: E.effectiveTime, PharyngitisCode: P.Code }

// This combine
let EncountersAndConditions =
	[Encounter, Performed: "Ambulatory/ED Visit"] E
		combine [Condition: "Acute Pharyngitis"] P 
			where P.effectiveTime overlaps after E.performedAtTime

// Is equivalent to nested foreach invocations
let CombinedEncountersAndConditions =
	expand
	(
		foreach E in [Encounter, Performed: "Ambulatory/ED Visit"]
			return
				foreach P in [Condition: "Acute Pharyngitis"]
					return tuple { Encounter: E, Condition: P }
	)

// followed by a filter
let EncountersAndConditions =
	CombinedEncountersAndConditions EC 
		where EC.Condition.effectiveTime overlaps after EC.Encounter.performedAtTime


// Operator definition
operator CumulativeDuration(Intervals: list<interval<Timestamp>>)
{
	return 
		Sum
		(
			foreach Interval in Collapse(Intervals) 
				return days between start of Interval and end of Interval
		)
}

let CombinedValueSet = "Ambulatory/ED Visit" union "Inpatient Visit"

let XYZCode = First(CombinedValueSet VS where VS.code = 'XYZ')
let XYZCode = tuple { code: 'XYZ' }

Sum({ 1, 2, 3, 4, 5 })
Count([Encounter, Performed])

Sum(result from [ObservationResult])


Sum(foreach R in [ObservationResult] return R.result)
Min(foreach E in [Encounter, Performed] return R.effectiveTime)


foreach R in [ObservationResult] return R.result


// foreach vs return clause

let Z = foreach X in [Encounter, Performed]
	return foreach Y in [ObservationResult] return tuple { X: X, Y: Y }

let A = [Encounter, Performed] X return [ObservationResult] Y return tuple { X: X, Y: Y }

// Aggregates

let A = Sum(result from [ObservationResult])
let A = Sum([ObservationResult] X return X.result)
let A = Sum({ 1, 2, 3, 4, 5 })

let Encounters = [Encounter, Performed, during MeasurementPeriod]
let EncountersWithDates = Encounters X return tuple { encounter: X, effectiveDate: date of X.effectiveTime }
let Dates = Distinct(EncountersWithDates X return X.effectiveDate)
let NumberOfEncountersPerDate = Dates D return tuple { date: D, number: Count(EncountersWithDates E where E.effectiveDate = D) }

// Slide Examples:

// Simple Example:

library CMS153 version '2'

using QUICK

context PATIENT

parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))

valueset "Female Administrative Sex" = ValueSet('2.16.840.1.113883.3.560.100.2')

let InDemographic = 
	AgeAt(start of MeasurementPeriod) >= 16
		and AgeAt(start of MeasurementPeriod) < 24
		and Gender in "Female Administrative Sex"

// Equivalent QDM
AND: "Patient Characteristic Birthdate: birth date" >= 16 year(s) starts before start of "Measurement Period"
AND: "Patient Characteristic Birthdate: birth date" < 24 year(s) starts before start of "Measurement Period"
AND: "Patient Characteristic Sex: Female"



// Population Criteria

// NOTE: Other clinically relevant indicators omitted for brevity
let SexuallyActive =
	exists ([Condition: "Chlamydia"] C where C.effectiveTime overlaps before MeasurementPeriod)
	or exists ([Condition: "HIV"] C where C.effectiveTime overlaps before MeasurementPeriod)
	or exists ([Condition: "Syphilis"] C where C.effectiveTime overlaps before MeasurementPeriod)

// Equivalent QDM
OR: " Diagnosis, Active: Chlamydia" overlaps before "Measurement Period"
OR: "Diagnosis, Active: HIV" overlaps before "Measurement Period"
OR: "Diagnosis, Active: Syphilis" overlaps before "Measurement Period"

// Chlamydia Screening, CQM

library CMS153_CQM version '2'

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
		and exists ([ObservationResult: "Chlamydia Screening"] R where R.effectiveTime during MeasurementPeriod)

// Chlamydia Screening, CDS

library CMS153_CDS version '2'

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
	not exists ([ObservationResult: "Chlamydia Screening", during interval[today - 1 years, today]])
	and not exists ([Procedure, Planned: "Chlamydia Screening", during interval[today, null]])
	and not exists ([ObservationResult: "Reason for not performing Chlamydia Screening"])

let NeedScreening = InDemographic and SexuallyActive and NoScreening


// Chlamydia Screening, Common

library Common version '1'

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

let ChlamydiaScreening = 
    [ObservationResult: "Chlamydia Screening"]

// Chlamydia Screening, CQM, Using Common

library CMS153_CQM version '2'

using QUICK

context PATIENT

include Common version '1'

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
    InDenominator and exists (Common.ChlamydiaScreening S where S.effectiveTime during MeasurementPeriod)


// Chlamydia Screening, CDS, Using Common

library CMS153_CDS version '2'

using QUICK

context PATIENT

include Common

valueset "Reason for not performing Chlamydia Screening" = ValueSet('TBD')

let InDemographic =
    Age() >= 16 and Age() < 24 and Gender in Common."Female Administrative Sex"

let SexuallyActive =
    exists (Common.ConditionsIndicatingSexualActivity)
    or exists (Common.ResultsIndicatingSexualActivity)

let NoScreening =
    not exists (Common.ChlamydiaScreening S where S.effectiveTime during interval[today - 1 years, today])
    and not exists ([Procedure, Planned: Common."Chlamydia Screening"] P where P.effectiveTime >= now)
    and not exists ([ObservationResult: "Reason for not performing Chlamydia Screening"])

let NeedScreening = InDemographic and SexuallyActive and NoScreening

