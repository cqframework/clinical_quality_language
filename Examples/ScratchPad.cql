// We could define same as generally:

A same as B
// is equivalent to
(A is null and B is null) or (A = B)

// Then Date/Time Equality could be defined as
A = B
// is equivalent to
year of A = year of B
	and month of A = month of B
	and day of A = day of B
	and hour of A = hour of B
	and minute of A = minute of B
	and second of A = second of B
	and millisecond of A = millisecond of B

A same as B
// is equivalent to
year of A same as year of B
	and month of A same as month of B
	and day of A same as day of B
	and hour of A same as hour of B
	and minute of A same as minute of B
	and second of A same as second of B
	and millisecond of A same as millisecond of B

Date(2014, 15, 2) same as Date(2014, 15, 2)
Date(2014, 15) same as Date(2014, 15, 2)


// and for precision-based equality
A same day as B
// is equivalent to
year of A same as year of B
	and month of A same as month of B
	and day of A same as day of B

// Date/Time Relative Comparison
A > B
// is equivalent to
year of A > year of B
	or (year of A = year of B and month of A > month of B)
	or (year of A = year of B and month of A = month of B and day of A > day of B)
	or (year of A = year of B and month of A = month of B and day of A = day of B and hour of A > hour of B)
	or (year of A = year of B and month of A = month of B and day of A = day of B and hour of A = hour of B and minute of A > minute of B)
	or (year of A = year of B and month of A = month of B and day of A = day of B and hour of A = hour of B and minute of A = minute of B and second of A > second of B)
	or (year of A = year of B and month of A = month of B and day of A = day of B and hour of A = hour of B and minute of A = minute of B and second of A = second of B and millisecond of A > millisecond of B)

Date(2012) > Date(2014, 2, 15) // Returns false
Date(2014) > Date(2014, 2, 15) // Returns null
Date(2016) > Date(2014, 2, 15) // Returns true

// Date/Time Equality Comparison
A >= B
// is equivalent to
A = B or A > B

// Date/Time Comparison w/ Relative Qualifier (at least/at most)
A at least same day as B
A at most same day as B
// is equivalent to
A same day as B or A > B
A same day as B or A < B


// Durations
days between A and B

// returns number of boundaries crossed

// extended to return an interval
days between Date(2014, 1, 15) and Date(2014, 2)

days between Date(2014, 1, 15) and Date(2014, 2, 1) // 17 days
days between Date(2014, 1, 15) and Date(2014, 2, 28) // 44 days
// returns interval[17, 44]

// define implicit conversion from a point to an interval
// allow interval comparison operators
// = (same as)
// > (after)
// >= (after or same as)
// < (before)
// <= (before or same as)
days between Date(2014, 1, 15) and Date(2014, 2) > 2
// results in
interval[17, 44] > [2, 2]
days between Date(2014, 1, 15) and Date(2014, 2) > 20
// results in 
interval[17, 44] > [20, 20] // potentially should return unknown

// Timing Phrase Translations
// same as
A same as B
A starts same as start B
A starts same day as start B
start of A same day as start of B

// at least/most same as
A starts at least same day as start B
A starts at most same day as start B

start of A at least same day as start of B
start of A at most same day as start of B

// before/after
A starts before start B
A starts after start B
start of A < start of B
start of A > start of B

// duration before/after
A starts 3 days before start B
A starts 3 days after start B
days between start of A and start of B = 3
days between start of A and start of B = -3

// at least/most duration before/after
A starts at least 3 days before start B
A starts at most 3 days after start B
days between start of A and start of B >= -3
days between start of A and start of B <= 3

// within duration
A starts within 3 days of start B
days between start of A and start of B in [-3, 3]

// during
A starts during B

// includes
A includes start B

[Encounter, Performance: "A"] A
  with [Condition: "B"] B
  with [Medication: "C"] C
  where A.effectiveTime overlaps after B.effectiveTime
  where C.effectiveTime overlaps after B.effectiveTime
  where A.effectiveTime overlaps after C.effectiveTime

define Medications =
	[Medication: "C"] C
		with [Condition: "B"] B
			where C.effectiveTime overlaps after B.effectiveTimdefinelet Encounters =
	[Encounter, Performance: "A"] A
		with [Condition: "B"] B where A.effectiveTime overlaps after B.effectiveTime
		with Medications C where A.effectiveTime overlaps after C.effectiveTime

Date(2014) < Date(2014, 1, 1)
Date(null)
Date(null, 5)
Date(2014, 5, null, 16, 30)


// Introduce the notion of an uncertainty range:
uncertainty[17, 44]

// Equality
uncertainty[17, 44] = 2 // returns false
uncertainty[17, 44] = 20 // returns unknown
uncertainty[17, 44] = 45 // returns false

// if the value being compared falls within the uncertainty range, the result is unknown, otherwise the result is false

uncertainty[17, 44] = uncertainty[17, 44] // returns unknown
uncertainty[17, 44] = uncertainty[2, 12] // returns false
uncertainty[17, 44] = uncertainty[2, 20] // returns unknown

X + Y

interval[17, 44] = interval[17, 44]

// If the uncertainty ranges overlap in any way, the result is unknown, otherwise the result is false

// Comparison
interval[17, 44] > 2 // returns true
uncertainty[17, 44] > 20 // returns unknown
uncertainty[17, 44] > 45 // returns false

// If the value being compared falls within the uncertainty range, the result is unknown, otherwise, the result is based on the comparison to the relevant boundary

uncertainty[17, 44] > uncertainty[17, 44] // returns unknown
uncertainty[17, 44] > uncertainty[2, 12] // returns true
uncertainty[17, 44] > uncertainty[2, 20] // returns unknown
uncertainty[17, 44] > uncertainty[20, 20] // returns unknown
uncertainty[17, 44] > uncertainty[20, 50] // returns unknown
uncertainty[17, 44] > uncertainty[45, 50] // returns false

// If the uncertainties overlap in any way, the result is unknown, otherwise, the result is based on the comparison to the relevant boundaries

// Arithmetic

uncertainty[17, 44] + 2 // returns uncertainty[19, 46]
interval[17, 44] + uncertainty[5, 10] // returns uncertainty[22, 54]
uncertainty[17, 44] * 2 // returns uncertainty[34, 88]
interval[17, 44] * uncertainty[2, 4] // returns uncertainty[34, 176]

// Membership

uncertainty[17, 44] in interval[-3, 3] // returns false
uncertainty[17, 44] in interval[1, 100] // returns true
uncertainty[17, 44] in interval[25, 50] // returns unknown

// If the uncertainty is before or after the interval, return false
// If the uncertainty is contained within the interval, return true
// If the uncertainty overlaps either interval boundary, return unknown
// These rules follow directly from the rules for comparison of uncertainties

// Interval granularities, for reals
// How do we deal with interval granularities for reals?
// Easiest conceptual solution is to assert that we only deal with BCDs, and that the relevant successor/predecessor functions are defined in terms of the minimum step for the real.
// This would require a precision and scale to be specified for all real values. We could infer a default of say 28,8. The point type of a real interval would then be based on the most-specific
// type of the start and stop for the interval. If you wanted an interval at a lower granularity, you would convert the real to the lower precision and scale prior to creation of the interval.

// Interval granularities, for date/times

// Datetimes must not be allowed to specify components with lower granularity than unspecified components
// Because what would it mean, for example, to have a DateTime with a month, but no year? I argue that if we need that, it's not a date/time.

// What would it mean to specify an interval with start/stop datetimes of different granularities?
// For example, interval[DateTime(2014), DateTime(2015, 1)]?
// The interval is somewhat poorly defined, but you could still answer the question, duration in years, which would be 1. duration in months or lower would be an uncertainty[1, 12]

// What would uncertainty over datetimes look like?
// If the low/high bounds of the uncertainty were not specified to the same granularity, the appropriate granularity would be defaulted
uncertainty[DateTime(2014), DateTime(2015, 1)] would become uncertainty[DateTime(2014, 1), DateTime(2015, 1)]
uncertainty[DateTime(2014, 1), DateTime(2015)] would become uncertainty[DateTime(2014, 1), DateTime(2015, 12)]

uncertainty[DateTime(2014, 1), DateTime(2015, 1)] + 1 year = uncertainty[DateTime(2015), DateTime(2016, 1)]
uncertainty[DateTime(2014), DateTime(2015, 1)] + 1 month = 

DateTime(2014) + 1 month = uncertainty[DateTime(2014, 1), DateTime(2015, 1)]
DateTime(2014) = uncertainty[DateTime(2014, 1), DateTime(2014, 12)]

// Does "fuzzy" logic apply? No.

// interval of an uncertainty...
interval[uncertainty[1, 10], uncertainty[20, 30]]

// uncertainty of an uncertainty....

uncertainty[uncertainty[1, 10], uncertainty[20, 30]] // equivalent to uncertainty[1, 30]

uncertainty[1, 5]
uncertainty[5, 10]

uncertainty[1, 4] > uncertainty[5, 10] // false
uncertainty[1, 4] >= uncertainty[5, 10] // false
uncertainty[1, 4] = uncertainty[5, 10] // false
uncertainty[1, 4] <= uncertainty[5, 10] // true
uncertainty[1, 4] < uncertainty[5, 10] // true

uncertainty[5, 10] > uncertainty[1, 4] // true
uncertainty[5, 10] >= uncertainty[1, 4] // true
uncertainty[5, 10] = uncertainty[1, 4] // false
uncertainty[5, 10] <= uncertainty[1, 4] // false
uncertainty[5, 10] < uncertainty[1, 4] // false

uncertainty[1, 5] > uncertainty[5, 10] // false
uncertainty[1, 5] >= uncertainty[5, 10] // unknown
uncertainty[1, 5] = uncertainty[5, 10] // unknown
uncertainty[1, 5] <= uncertainty[5, 10] // true
uncertainty[1, 5] < uncertainty[5, 10] // unknown

uncertainty[5, 10] > uncertainty[1, 5] // unknown
uncertainty[5, 10] >= uncertainty[1, 5] // true
uncertainty[5, 10] = uncertainty[1, 5] // unknown
uncertainty[5, 10] <= uncertainty[1, 5] // unknown
uncertainty[5, 10] < uncertainty[1, 5] // false

uncertainty[1, 6] > uncertainty[5, 10] // unknown
uncertainty[1, 6] >= uncertainty[5, 10] // unknown
uncertainty[1, 6] = uncertainty[5, 10] // unknown
uncertainty[1, 6] <= uncertainty[5, 10] // unknown
uncertainty[1, 6] < uncertainty[5, 10] // unknown

uncertainty[5, 10] > uncertainty[1, 6] // unknown
uncertainty[5, 10] >= uncertainty[1, 6] // unknown
uncertainty[5, 10] = uncertainty[1, 6] // unknown
uncertainty[5, 10] <= uncertainty[1, 6] // unknown
uncertainty[5, 10] < uncertainty[1, 6] // unknown

parameter "Measure Period" default interval[Date(2012, 1, 1, 0, 0, 0), Date(2013, 1, 1, 0, 0, 0))

define MyEncounter.low = Date(2012, 1, 1)

MyEncounter starts same day or after start of "Measure Period"

MyEncounter during "Measure Period"

define MyEncounter.effectiveTime = interval[Date(2012), Date(2012)]

MyEncounter during "Measure Period"

interval[Date(2012), Date(2012)] during interval[Date(2012, 1, 1, 0, 0, 0), Date(2013, 1, 1, 0, 0, 0))

Date(2012) >= Date(2012, 1, 1, 0, 0, 0) and Date(2012) <= Date(2012, 12, 31, 23, 59, 59)

Date(2012) >= Date(2012, 1, 1, 0, 0, 0)

Date(2012) = Date(2012, 1, 1, 0, 0, 0) or Date(2012) > Date(2012, 1, 1, 0, 0, 0)


define A = uncertainty[0, 5]
define B = uncertainty[5, 10]

A = B // unknown
A < B // unknown
A <= B // unknown
A > B // false
