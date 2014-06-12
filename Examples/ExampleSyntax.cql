

// Data Type examples
// Simple Types
let BooleanLiteral = true
let StringLiteral = "female"
let NumberLiteral = 16

// Clinical Types
let QuantityLiteral = 6 u"gm/cm3"  // UCUM units
let TimespanLiteral = 3 months

// Structured Types
let Encounters = [Encounter, Performed]
let TupleLiteral = tuple { Name: "Patrick", DOB: Date(2014, 1, 1) }

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
let ConvertedDate = convert "20140101120000-0600" to Timestamp
let DateAdd = today + 3 months - 2 days
let DateDiff = months between start of X and end of X // TODO: Still working on this.... between ... and is tricky...
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





