

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
let IntervalLiteral = interval[today - 1 years, now]

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
let DateDiff = months between start of effectiveTime and end of effectiveTime // TODO: Still working on this.... between ... and is tricky...
let Months = month of start of effectiveTime

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

